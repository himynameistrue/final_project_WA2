package it.polito.wa2.catalog.repositories

import it.polito.wa2.catalog.domain.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: CrudRepository<User, Long> {
    fun findByEmail (email: String): User?
    fun findByRolesContaining(role: String): List<User>
}