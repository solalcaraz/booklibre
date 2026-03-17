import type { UltimasResenia } from "../../types/DetalleReserva"

interface Props{
    ultimasResenias: UltimasResenia[]
}

export const LibroResenias = ({ultimasResenias}: Props)=>{
    return(
        <div className="bg-gray-400">
            {ultimasResenias.map((resenia)=>(
                <div key={resenia.id} >
                    <p>{resenia.nombreUsuario}</p>
                    <p>{resenia.puntuacion}</p>
                    <p>{resenia.comentario}</p>
                </div>
            ))}
        </div>
    )
}