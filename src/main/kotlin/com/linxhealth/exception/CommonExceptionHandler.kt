package com.linxhealth.exception

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Singleton

@Serdeable
data class ErrorResponse(val message: String)

@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Requires(classes = [NotFoundException::class, ExceptionHandler::class])
class NotFoundExceptionHandler : ExceptionHandler<NotFoundException, HttpResponse<ErrorResponse>> {
    override fun handle(
        request: HttpRequest<*>,
        exception: NotFoundException
    ): HttpResponse<ErrorResponse> =
        HttpResponse.notFound(ErrorResponse(exception.message ?: "Resource not found"))
}

@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Requires(classes = [ValidationException::class, ExceptionHandler::class])
class ValidationExceptionHandler : ExceptionHandler<ValidationException, HttpResponse<ErrorResponse>> {
    override fun handle(
        request: HttpRequest<*>,
        exception: ValidationException
    ): HttpResponse<ErrorResponse> =
        HttpResponse.badRequest(ErrorResponse(exception.message ?: "Validation failed"))
}

@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Requires(classes = [ConflictException::class, ExceptionHandler::class])
class ConflictExceptionHandler : ExceptionHandler<ConflictException, HttpResponse<ErrorResponse>> {
    override fun handle(
        request: HttpRequest<*>,
        exception: ConflictException
    ): HttpResponse<ErrorResponse> =
        HttpResponse.status<ErrorResponse>(HttpStatus.CONFLICT)
            .body(ErrorResponse(exception.message ?: "Conflict"))
}
