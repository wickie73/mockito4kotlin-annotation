# Mockito Annotations for Kotlin

![Kotlin](https://img.shields.io/badge/Kotlin-1.2%2B-blue.svg)
![Mockito](https://img.shields.io/badge/Mockito-2.13%2B-blue.svg)
[![DUB](https://img.shields.io/dub/l/vibe-d.svg)](https://github.com/wickie73/mockito4kotlin.annotation/blob/master/LICENSEhttps://github.com/wickie73/mockito4kotlin.annotation/blob/master/LICENSE)

This is a small Kotlin library which supports Annotations for Mockito or Kotlin libraries based on Mockito like 
[Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin/) or [Mockito4k](https://github.com/tmurakami/mockito4k). 

It works like [MockitoAnnotations.initMocks(testClass)](https://static.javadoc.io/org.mockito/mockito-core/2.13.0/org/mockito/MockitoAnnotations.html)
and is full compatible.

The current library can be found in [dist](dist).

## Examples

Mock with Annotation:
```kotlin
@Mock
lateinit var addressDAO: AddressDAO
@Mock
lateinit var address: Address

@Before
fun setUp() {
    MockAnnotations.initMocks(this)
}

@Test
fun testService() {
    val addressList = listOf(address)

    // with Mockito
    `when`(addressDAO.getAddressList()).thenReturn(addressList)

    // or with Mockito-Kotlin
    whenever(addressDAO.getAddressList()).thenReturn(addressList)
}
```

Spy with Annotation:

```kotlin
@Spy
var addressDAO = AddressDAO()
@Mock
lateinit var address: Address

@Before
fun setUp() {
    MockAnnotations.initMocks(this)
}

@Test
fun testService() {
    val addressList = listOf(address)

    // with Mockito
    `when`(addressDAO.getAddressList()).thenReturn(addressList)

    // or with Mockito-Kotlin
    whenever(addressDAO.getAddressList()).thenReturn(addressList)
}
```

Capture with Annotation:

```kotlin
@Captor
lateinit var captor: ArgumentCaptor<Address>
@Mock
lateinit var addressDAO: AddressDAO

@Before
fun setUp() {
    MockAnnotations.initMocks(this)
}

@Test
fun testService() {
    val address: Address().apply {
        street = "Abbey Road 73"
        city = "London"
    }

    addressDAO.save(address)

    verify(addressDAO).save(captor.capture())
    assertEquals(address, captor.value)
}

interface AddressDAO {
    fun getAddressList(): List<Address>
    fun save(address: Address?)  // 'Address?' has to be nullable here
}
```

Inject Mocks with Annotation:

```kotlin
@Spy
lateinit var addressList: List<Address>
@Mock
lateinit var addressDatabase: AddressDatabase
@InjectMocks
val addressDAO: AddressDAOImpl()

@Before
fun setUp() {
    MockAnnotations.initMocks(this)
}

@Test
fun testService() {
    // with Mockito
    `when`(addressList.size()).thenReturn(2)
    
    // or with Mockito-Kotlin
    whenever(addressList.size()).thenReturn(2)

    verify(addressDatabase).addListener(any(ArticleListener.class));

    assertEquals(addressList, addressDAO.addressList)
    assertEquals(addressDatabase, addressDAO.addressDatabase)
    assertThat(addressDAO.addressList).hasSize(2)
}

class AddressDAOImpl {
    lateinit var addressList: List<Address>
    lateinit var addressDatabase: AddressDatabase
}
```

## Limitations

Stubbing does not work with

* immutable properties ( `val address: Address()` )
* properties of final classes (use `interface` or `open class` )
* properties of sealed classes (only `@Spy` )
* properties of private/internal inner classes
* properties of companion objects
* properties of objects
* delegated properties ( `var p: String by Delegate()` )

Instead stubbing works with

* properties in sealed classes
* properties in private/internal inner classes
* properties in companion objects
* properties in objects
* properties in data classes
* properties of data classes
