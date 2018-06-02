# Mockito Annotations for Kotlin

![Kotlin](https://img.shields.io/badge/Kotlin-1.2%2B-blue.svg?longCache=true)
![Mockito](https://img.shields.io/badge/Mockito-2.13%2B-blue.svg?longCache=true)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg?longCache=true)](https://github.com/wickie73/mockito4kotlin.annotation/blob/master/LICENSE)

![Travis.Build](https://travis-ci.org/wickie73/mockito4kotlin.annotation.svg?longCache=true)

This is a small Kotlin library which supports Annotations for Mockito or Kotlin libraries based on Mockito like 
[Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin/) or [Mockito4k](https://github.com/tmurakami/mockito4k). 

In this library the initialization of fields annotated with Mockito annotations by code  
`MockitoAnnotations.initMocks(testClass)`
 is replaced by  
`MockAnnotations.initMocks(testClass)`
which is written in Kotlin and supports most of Kotlin specific features. 
It is compatible with [MockitoAnnotations.initMocks(testClass)](https://static.javadoc.io/org.mockito/mockito-core/2.15.0/org/mockito/MockitoAnnotations.html).

## Content
* [Installing](#installing)
* [Examples](#examples)
* [Limitations](#limitations)
* [@KCapture vs. @Captor Annotation](#kcapture-vs-captor-annotation)

Installing
----------

Mockito Annotations for Kotlin is available on jcenter.   
### gradle
```gradle
testcompile 'org.mockito4kotlin:annotation:0.2.7'
```
### maven
```xml
<dependency>
    <groupId>org.mockito4kotlin</groupId>
    <artifactId>annotation</artifactId>
    <version>0.2.7</version>
    <scope>test</scope>
</dependency>

<repository>
    <snapshots>
        <enabled>false</enabled>
    </snapshots>
    <id>bintray-wickie73-maven</id>
    <name>bintray</name>
    <url>https://bintray.com/wickie73/wickieMaven</url>
</repository>
```
Examples
--------

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

ArgumentCaptor with Annotation:

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

[Mockito-Kotlins](https://github.com/nhaarman/mockito-kotlin) KArgumentCaptor with KCapture Annotation:

```kotlin
@KCaptor
lateinit var captor: KArgumentCaptor<Address>
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
    assertEquals(address, captor.firstValue)
}

interface AddressDAO {
    fun getAddressList(): List<Address>
    fun save(address: Address)  // 'Address' has not to be nullable here
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

Limitations
-----------

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

@KCapture vs. @Captor Annotation
--------------------------------

Mockitos `ArgumentCaptor#capture()` returns null. So like in this example
the type of the argument of method `save(address: Address?)` in interface `AddressDAO` has to be `nullable`:
```kotlin
@Captor
lateinit var captor: ArgumentCaptor<Address>
// ...
MockAnnotations.initMocks(this)
// ...
verify(addressDAO).save(captor.capture())
// with: 
interface AddressDAO {
    fun save(address: Address?)  // 'Address?' has to be nullable here
}
```
With [Mockito-Kotlins](https://github.com/nhaarman/mockito-kotlin) KArgumentCaptor you don't have to be care about 
`nullable` parameters: 
```kotlin
@KCaptor
lateinit var captor: KArgumentCaptor<Address>
// ...
MockAnnotations.initMocks(this)
// ...
verify(addressDAO).save(captor.capture())
// with: 
interface AddressDAO {
    fun save(address: Address)  // 'Address' has not to be nullable here
}
```
