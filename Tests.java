class Content {

  int accessTotal;
  Object content;

  Content( Object content ) {
    this.accessTotal = 0;
    this.content = content;
  }

  void incrementAccessTotal() {
    // overFlow logic ...
    accessTotal ++;
  }

  int getAccessTotal() {
    return accessTotal;
  }


}
