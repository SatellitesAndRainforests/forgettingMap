

class Content {

  private int accessTotal;
  private Object contents;
  private long created;

  Content( Object contents ) {
    this.accessTotal = 0;
    this.contents = contents;
    this.created = System.currentTimeMillis();
  }

  synchronized void incrementAccessTotal() {
    // overFlow logic ...
    accessTotal ++;
  }

  int getAccessTotal() {
    return accessTotal;
  }

  Object getContents(){
    return contents;
  }

  // not used
  long getCreated(){
    return created;
  }

}


