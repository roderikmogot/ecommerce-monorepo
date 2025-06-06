package com.example.ecommercebackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class EcommerceBackendApplication

fun main(args: Array<String>) {
    runApplication<EcommerceBackendApplication>(*args)
}