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


-- Testing --------------------------------------

+ Many threads
+ important aspects.


