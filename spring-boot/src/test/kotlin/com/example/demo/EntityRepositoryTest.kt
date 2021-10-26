package com.example.demo

import com.example.demo.persistance.Entity
import com.example.demo.persistance.EntityRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class EntityRepositoryTest {

    @Autowired
    private lateinit var entityReposity: EntityRepository

    @Test
    fun `findById should find entity`() {
        // given
        val savedEntity = entityReposity.save(Entity(name = "name"))

        // when
        val foundEntity = entityReposity.findById(savedEntity.id!!)

        // then
        assertTrue { foundEntity.get() == savedEntity}
    }
}