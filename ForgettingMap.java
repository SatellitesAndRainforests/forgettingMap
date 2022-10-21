import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ForgettingMap {

  int maxSize;
  int currentSize;
  ConcurrentHashMap<Integer,Content> forgettingMap;

  ForgettingMap( int x ){

    if (x<1 || x>500) throw new IllegalArgumentException("The max value must be in range (1 - 500");

    this.maxSize = x;
    this.currentSize = 0;
    // initial table size is x. prevents internal dynamic resizing.
    forgettingMap = new ConcurrentHashMap<Integer, Content>(x);

  }


  synchronized void add(int key, Content value) {

    if (forgettingMap.containsKey(key)) {
      forgettingMap.put(key,value);
      return;
    }

    if (currentSize < maxSize) {
      forgettingMap.put(key,value);
      this.currentSize ++;
      return;
    }

    //forgettingMap is at capacity
    deleteLeastAccessed();
    forgettingMap.put(key,value);

  //  if (forgettingMap.mappingCount() > maxSize) throw new IllegalStateException("max size > " + maxSize + " is: " + forgettingMap.mappingCount());

  }

  private void deleteLeastAccessed() {

    // an overflowing accessCount will become the lowest.
    int lowestAccessCount = 0;
    // in case of a tie-breaker the last found is deleted to keep the time O(n).
    int leastAccessedKey = 0;

    for (Map.Entry<Integer,Content> entry: forgettingMap.entrySet()) {
      if (entry.getValue().getAccessTotal() <= lowestAccessCount) {
        lowestAccessCount = entry.getValue().getAccessTotal();
        leastAccessedKey = entry.getKey();
      }
    }

    forgettingMap.remove(leastAccessedKey);
  }


  Content find(int key) {
    Content content = forgettingMap.get(key);
    if (content != null) content.incrementAccessTotal();
    return content;
  }



}












