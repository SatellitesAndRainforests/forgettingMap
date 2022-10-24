# forgettingMap
A 'forgetting map' holds associations between 'key' and 'content' with add(association) and find(key) --> content, methods.


-- Objective --------------------------

Design, Implement, Test a thread-safe 'forgetting map'.

+ Hold as many assoications as it can
+ but no more than x at any time.
+ x being an argument passed to the constructor

+ The association least used is removed from the map - when a new entry requires a space and the map is at capacity.
+ least used = number of times each association has been retrieved by the find method. 
+ where there are mutliple least-used a tie a suitable tie breaker should be used.

+ Assume the forgetting map will be at capacity for most of its life. 
+ the find() will be used as often as the put method().

+ Java is preferable
+ You may use standard built in collection data structures, but not pre-built library soloutions. 


-- Design ------------------------------

+ has to be a count of times a value is accessed. can order in the collection by this count ?

+ Fast look up (HashTable)
+ HashMap<int, Content> 
+ LinkedHashMap<int, Content>  //A hashMap that can remember the order associations were inserted or which were last accessed.
  useful for the tie-breaker?
+ synchronisedHashmap vs ConcurrentHashMap

+ tried:
+ concurrentSkipListMap<Key,Value> with comparator.
+ but only the key can be sorted not the access total of the value

+ concurrentHashMap
+ Google Guava ...
+ Concurrent access makes the data transient so that viewing data ... snapshots ...


Content {
  int timesAccessed = 0;
  Object content = null;
  Content(Object content) {
  }
}

ForgettingMap {
  int maxSize = 100;
  ForgettingMap( int size  ) {
    if ( size < 1 || size > maxSize ) throw error.
  }
}

// sncyronised //
boolean add(key, Content) { 
  if (Content == null) return false
  if (forgettingMapIsAtCapacity()) removeLeastAccessed()
}

removeLeastAccessed() {
  Collection leastAccessed = getLeastAccessed()
  remove oldest.   
}

Content find(key) { }

+ The concurrnetHashMap was used at it was considered to be highly scalable suitable for use in the Oragami project. 

+ bottle-neck is the syncronised add. synchronisation can be defined internally in the method to allow for concurrent adds - but with added complexity - the concurrent nature of the forgetting map means operating on transient data that retrieving the size(), or 'least used' association is ever updating/changing. there is a 'happens before' relationship within the concurrentHashMap but there is a possibility finding and deleting the least accessed, and, ensuring concurrnet threads dont increase the map beyond the x maxSize param when not synchronised may occur.  

+ I think there is an O(log(n)) optimised ForgettingMap using a <accessCount, Key> Map that can store duplicate keys with a comparator to keep orded by accessCount. The first key can be used to retrieve the key for a seconf Map storing <Key, Value> assoications.

+ Does not use unique keys. Unique keys increases the compleixty of testing where 

-- Testing --------------------------------------

+ Many threads
+ important aspects.

+ Tests:
++ Thread-safety
++ The map holds the associations between 'key' and 'content'.
++ add() add's an association.
++ find() retrieves the same assoication - and increments the assoication's 'retreved count'.
++ Hold's as many as the maxSize, but no more.
++ test the x size parameter.
++ test the least used association is removed - when at capacity.
++ test the tiebreaker ...
++ test differnt inputs (-, int overflow, ...);


+ Many Threads Tests:
++ How many adds() and reads() per hour with x threads with my:    2 cores, 4 threads, 2012 'Intel i7-3520M (2.90 GHz, 4MB L3, 1600MHz FSB'


