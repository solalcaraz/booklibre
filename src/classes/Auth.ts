export type AuthFormData = {
  fullName: string
  email: string
  password: string
  confirmPassword: string
}

export type AuthErrors = Partial<Record<keyof AuthFormData, string>>

export class Auth {
  constructor(
    public email: string,
    public password: string,
    public confirmPassword?: string,
    public fullName?: string
  ) {}

  validateLogin() {
    const errors: AuthErrors = {}

    this.validateEmail(errors)
    this.validateLoginPassword(errors)

    return errors
  }

  validateRegister() {
    const errors: AuthErrors = {}

    this.validateFullName(errors)
    this.validateEmail(errors)
    this.validateRegisterPassword(errors)

    return errors
  }

  private validateFullName(errors: AuthErrors): void {
    if (!this.fullName?.trim()) {
      errors.fullName = 'Nombre requerido'
    }
  }

  private validateEmail(errors: AuthErrors): void {
    if (!this.email?.trim()) {
      errors.email = 'Email requerido'
      return
    }

    if (!this.email.includes('@')) {
      errors.email = 'Email inválido'
    }
  }

  private validateLoginPassword(errors: AuthErrors): void {
    if (!this.password) {
      errors.password = 'Contraseña requerida'
    }
  }

  private validateRegisterPassword(errors: AuthErrors): void {
    if (!this.password) {
      errors.password = 'Contraseña requerida'
      return
    }

    if (this.password.length < 5) {
      errors.password = 'La contraseña debe tener al menos 5 caracteres'
      return
    }

    if (!this.confirmPassword) {
      errors.confirmPassword = 'Por favor confirme su contraseña'
      return
    }

    if (this.password !== this.confirmPassword) {
      errors.confirmPassword = 'Las contraseñas no coinciden'
    }
  }

  static hasErrors(errors: AuthErrors): boolean {
    return Object.keys(errors).length > 0
  }
}

/* toLoginDTO(): LoginDTO {
    return {
      username: this.username,
      password: this.password
    }
  } */
