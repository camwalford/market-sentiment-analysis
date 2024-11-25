package me.camwalford.backendapiservice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table


@Entity
@Table(name = "request")
data class Request(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(nullable = false)
    val uri: String = "",

    @Column(nullable = false)
    val method: String = "",

    @Column(nullable = false)
    var total: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
)
