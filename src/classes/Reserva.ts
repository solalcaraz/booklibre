import { Libro } from './Libro'
import { Usuario } from './Ususario'

export class Reserva {
  libro: Libro = new Libro()
  prestadoA: Usuario = new Usuario()
  activo: boolean = false


}
