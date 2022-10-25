# forgettingMap
A thread-safe 'forgetting map' holds 'key' and 'content' associations with add(key,content) and find(key) --> content, methods.

Written in Java 1.8. Simply compile, and run in a commandline terminal:
javac *.java
java SingleAndMultiThreadTests

About   2,000,000   read/write operations per second for a ForgettingMap of size 10.
About   200,000     for a Forgettingmap of size 500. 

With slightly more writes than reads.
Tested on 2 cores, 4 threads, 2012 Intel i7, 2.90 Ghz.

The forgettingMap is built around the ConcurrentHashMap structure with <Integer, Content> key, value associations.
Synchronized add()'s and the Content Class's incrementAccessTotal() methods prevent lost updates, prevent the size increasing beyond the max size defined byt the argument 'x' and order multiple Threads to find and delete the least accessed association before it is found or deleted by another.

In case of many associations being the least accessed the first that was found is deleted to achieve a time-complexity of O(n).
A Content objects creationDate could also be used but with added overhead.
If the integer access count overflows it becomes the least accessed and is deleted. 

The ConcurrentHashMap structure has it's own thread-safety with a 'happens-before' guarentee that is expected to affect the read/write operation's speed. This is unexpected as the forgettingMap's find() method is not Synchronised. It was selected for it's thread-safety and it's high scaleability and relevent to the Oragarmi organisation.

Testing addresses both single and multi-threaded tests. Several of the tests revealed bugs that were then resolved. The testing coverage is not complete and a few further TODO: tests are outlined. Testing transient data increases the complexity of testing, but the testing is considered an appropriate start.

The Synchronised add() can be see as a bottleneck with an O(n) time-complexity. An O(log(n)) optimisation was explored where assoications 'access counts' can be sorted in a tree/concurrentSkipListMap with a comparator, although only sortable by keys and not by values. If an 'accessCount' key can reference many 'key' values, the set of least accessed 'keys' could be accessed in O(log(n)) time, significantly faster (even if the whole object were locked) but with added complexity to implement and test. Other considerations include duplicate keys, key,value pairs with many values.

I think there may be a trick with a stack aswell ...

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

-- Design notes ------------------------

+ has to be a count of times a value is accessed. can order in the collection by this count ?

+ Fast look up (HashTable)
+ HashMap<int, Content>  Simple key, easy to test.
+ LinkedHashMap<int, Content>  //A hashMap that can remember the order associations were inserted or which were last accessed. Useful for the tie-breaker?
+ synchronisedHashmap vs ConcurrentHashMap

+ tried:
+ concurrentSkipListMap<Key,Value> with comparator.
+ but only the key can be sorted not the access total of the value
+ thread-safe tree ? KISS

+ concurrentHashMap
+ Google Guava ...
+ Concurrent access makes the data transient so that viewing data is in snapshots ...

Rough Outline:

class Content {
  int timesAccessed = 0;
  Object contents = null;

  Content(Object contents) {
    this.contents = contents;
  }
}

ForgettingMap {
  int maxSize = ?;
  ForgettingMap( int size ) {
    if ( size < 1 || size > maxSize ) throw error.
  }

  // sncyronised //
  boolean add(key, Content) { 
  if (Content == null) return false
  if (forgettingMapIsAtCapacity()) removeLeastAccessed()
  put();
}

  removeLeastAccessed() {
    Collection leastAccessed = getLeastAccessed()
    remove oldest.   
}

Content find(key) { }


+ The concurrent nature of the forgetting map means operating on transient data, that retrieving the size(), or 'least used' association is ever updating/changing. 
+ Ensuring concurrnet threads dont increase the map beyond the x maxSize param when not synchronised may occur.
+ Does not use unique keys. int keys are easily/accidently overwritten with a new association. 


-- Testing notes --------------------------------------

+ Many threads
+ important aspects.

+ Tests:
++ Thread-safety
++ lost updates.
++ The map holds the associations between 'key' and 'content'.
++ find() retrieves the same assoication - and increments the assoication's 'retreved count'.
++ Hold's as many as the maxSize, but no more.
++ test the x size parameter.
++ test the least used association is removed - when at capacity.
++ test the tiebreaker ...
++ test differnt inputs (-, int overflow, ...);

+ Many Threads speed Tests:
++ How many adds() and reads() per hour with x threads with my:    2 cores, 4 threads, 2012 'Intel i7-3520M (2.90 GHz, 4MB L3, 1600MHz FSB'


