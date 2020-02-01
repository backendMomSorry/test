package weather.repositories

import org.springframework.data.repository.CrudRepository

class MockRepository<R : CrudRepository<T, ID>, T, ID>(
        val mock: R, // repository mock
        private val entities: MutableList<T> = ArrayList(),
        private val idExtractor: (T) -> ID?
) {



}
