# My notes
Use 2d array for board, see photo for class diagram

~ data storage options for storing piece moves ~
Collection
  List
      ArrayList
      LinkedList
  Set
    TreeSet
    HashSet // probably use this one

See photo for package organization

Use @Override when overriding an inherited class (ex: toString)

To eliminate moves for checkmate, just simulate each move and see if it puts the king in danger- if it does, remove it.
