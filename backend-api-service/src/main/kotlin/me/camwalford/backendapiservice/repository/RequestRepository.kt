// package: me.camwalford.backendapiservice.repository

package me.camwalford.backendapiservice.repository

import me.camwalford.backendapiservice.model.Request
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RequestRepository : JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE r.user.id = :userId AND r.uri = :uri AND r.method = :method")
    fun findByUserIdAndUriAndMethod(userId: Long, uri: String, method: String): Request?

    fun findByUserId(userId: Long): List<Request>

    fun findByUri(uri: String): List<Request>

    @Query("SELECT r.uri, r.method, SUM(r.total) FROM Request r GROUP BY r.uri, r.method")
    fun findTotalRequestsGroupedByUriAndMethod(): List<Array<Any>>

    @Query("SELECT r.user.id, SUM(r.total) FROM Request r GROUP BY r.user.id")
    fun findTotalRequestsGroupedByUser(): List<Array<Any>>

    @Query("SELECT COUNT(r) FROM Request r WHERE r.user.id = :userId")
    fun findTotalRequestsForUser(userId: Long): Optional<Long>

}