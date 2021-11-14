package com.example.interview.app

import java.util.UUID

final case class Product(id: UUID,
                         name: String,
                         price: Int)
object Product {

}
