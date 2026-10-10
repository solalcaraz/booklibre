package ar.edu.unsam.phm.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.FORBIDDEN)
class ForbiddenException(msg: String) : RuntimeException(msg)