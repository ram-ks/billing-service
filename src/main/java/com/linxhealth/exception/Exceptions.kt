package com.linxhealth.exception

class NotFoundException(message: String) : RuntimeException(message)
class ValidationException(message: String) : RuntimeException(message)
class ConflictException(message: String) : RuntimeException(message)