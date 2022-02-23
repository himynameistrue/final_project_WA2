package it.polito.wa2.order.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.dto.AuthorizeCommentDTO
import it.polito.wa2.dto.CommentDTO
import it.polito.wa2.dto.ProductDTO
import it.polito.wa2.order.services.OrderService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/products")
class ProductController(val orderService: OrderService) {

    @PutMapping("/{productId}/comments")
    fun addComment(
        request: HttpServletRequest,
        @PathVariable productId: Long,
        @RequestBody authCommentDTO: AuthorizeCommentDTO
    ): ProductDTO? {

        if (!orderService.hasUserBought(authCommentDTO.userId, productId)) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "You cannot create a comment for a product you did not purchase"
            )
        }

        val uri = URI(
            "http",
            null,
            "warehouse",
            8084,
            request.requestURI,
            request.queryString,
            null
        )

        val commentDTO = CommentDTO(
            authCommentDTO.title,
            authCommentDTO.body,
            authCommentDTO.stars
        )

        val restResponse: ResponseEntity<ProductDTO>
        try {
            restResponse = RestTemplate().exchange(
                uri,
                HttpMethod.PUT,
                HttpEntity<CommentDTO>(commentDTO),
                ProductDTO::class.java
            )

        } catch (e: RestClientResponseException) {
            val mapper = ObjectMapper()
            val incomingException = mapper.readTree(e.responseBodyAsString)
            throw ResponseStatusException(HttpStatus.resolve(e.rawStatusCode)!!, incomingException.path("message").textValue())
        }

        return restResponse.body!!
    }

}
