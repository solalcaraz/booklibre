#!/usr/bin/env bash
set -euo pipefail

wait_for_mongo() {
  local host="$1"
  until mongosh --quiet --host "$host" --eval "db.adminCommand({ ping: 1 }).ok" >/dev/null 2>&1; do
    sleep 2
  done
}

init_replica_set() {
  local host="$1"
  local config="$2"

  mongosh --quiet --host "$host" --eval "
    try {
      rs.status().ok
    } catch (e) {
      rs.initiate($config)
    }
  "
}

wait_for_primary() {
  local host="$1"
  until mongosh --quiet --host "$host" --eval "rs.status().members.some(member => member.stateStr === 'PRIMARY')" >/dev/null 2>&1; do
    sleep 2
  done
}

wait_for_mongo configsvr1
wait_for_mongo shard1a
wait_for_mongo shard2a

init_replica_set configsvr1 '{
  _id: "config-rs",
  configsvr: true,
  members: [
    { _id: 0, host: "configsvr1:27017" },
    { _id: 1, host: "configsvr2:27017" },
    { _id: 2, host: "configsvr3:27017" }
  ]
}'

init_replica_set shard1a '{
  _id: "shard1-rs",
  members: [
    { _id: 0, host: "shard1a:27017" },
    { _id: 1, host: "shard1b:27017" },
    { _id: 2, host: "shard1c:27017" }
  ]
}'

init_replica_set shard2a '{
  _id: "shard2-rs",
  members: [
    { _id: 0, host: "shard2a:27017" },
    { _id: 1, host: "shard2b:27017" },
    { _id: 2, host: "shard2c:27017" }
  ]
}'

wait_for_primary configsvr1
wait_for_primary shard1a
wait_for_primary shard2a
wait_for_mongo mongos

mongosh --quiet --host mongos --eval '
  const admin = db.getSiblingDB("admin");
  const existingShards = admin.runCommand({ listShards: 1 }).shards.map(shard => shard._id);
  const booklibre = db.getSiblingDB("booklibre_mongodb");

  if (!existingShards.includes("shard1-rs")) {
    sh.addShard("shard1-rs/shard1a:27017,shard1b:27017,shard1c:27017");
  }

  if (!existingShards.includes("shard2-rs")) {
    sh.addShard("shard2-rs/shard2a:27017,shard2b:27017,shard2c:27017");
  }

  sh.enableSharding("booklibre_mongodb");

  const libros = booklibre.libros;
  libros.createIndex({ "dueno._id": "hashed" });
  libros.createIndex({ "dueno._id": 1, activo: 1 });
  libros.createIndex({
    "dueno._id": 1,
    activo: 1,
    "reservasPendientes.fechaInicio": 1,
    "reservasPendientes.fechaFin": 1
  });

  const config = db.getSiblingDB("config");
  const librosYaShardeada = config.collections.findOne({ _id: "booklibre_mongodb.libros" }) !== null;

  if (!librosYaShardeada) {
    sh.shardCollection("booklibre_mongodb.libros", { "dueno._id": "hashed" });
  }

  booklibre.clicks_libros.createIndex({ libroId: 1 });

  const vistaLibroMasClickeado = "libro_mas_clickeado";
  if (booklibre.getCollectionInfos({ name: vistaLibroMasClickeado }).length > 0) {
    booklibre.getCollection(vistaLibroMasClickeado).drop();
  }

  booklibre.createView(vistaLibroMasClickeado, "clicks_libros", [
    {
      $group: {
        _id: {
          libroId: "$libroId",
          libroTitulo: "$libroTitulo"
        },
        clicks: { $sum: 1 }
      }
    },
    { $sort: { clicks: -1, "_id.libroTitulo": 1 } },
    { $limit: 1 },
    {
      $project: {
        _id: 0,
        libroId: "$_id.libroId",
        libroTitulo: "$_id.libroTitulo",
        clicks: 1
      }
    }
  ]);

  const vistaCantidadColeccionables = "cantidad_libros_coleccionables";
  if (booklibre.getCollectionInfos({ name: vistaCantidadColeccionables }).length > 0) {
    booklibre.getCollection(vistaCantidadColeccionables).drop();
  }

  booklibre.createView(vistaCantidadColeccionables, "libros", [
    {
      $match: {
        "_class": "COLECCIONABLE"
      }
    },
    {
      $count: "total_coleccionables"
    }
  ]);
'

echo "Mongo sharded cluster initialized."
