# POE-PART-3
PS C:\Users\sizwe\Downloads\Assignment 1 Project> Rename-Item "src\Assignment 1" "Assignment_1"
PS C:\Users\sizwe\Downloads\Assignment 1 Project> cd src
PS C:\Users\sizwe\Downloads\Assignment 1 Project\src> javac Assignment_1\jay.java Assignment_1\Login.java Assignment_1\Message.java
PS C:\Users\sizwe\Downloads\Assignment 1 Project\src> java Assignment_1.jay
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.trim()" because "this.message" is null
        at Assignment_1.Message.createMessageHash(Message.java:115)
        at Assignment_1.Message.<init>(Message.java:36)
        at Assignment_1.jay.sendMessagesOption(jay.java:170)
        at Assignment_1.jay.main(jay.java:105)
PS C:\Users\sizwe\Downloads\Assignment 1 Project\src> 
PS C:\Users\sizwe\Downloads\Assignment 1 Project\src> javac Assignment_1\jay.java Assignment_1\Login.java Assignment_1\Message.java
PS C:\Users\sizwe\Downloads\Assignment 1 Project\src> java Assignment_1.jay                    
Exception in thread "main" java.lang.NumberFormatException: For input string: ""
        at java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:67)
        at java.base/java.lang.Integer.parseInt(Integer.java:542)
        at java.base/java.lang.Integer.parseInt(Integer.java:662)
        at Assignment_1.jay.storedMessagesMenu(jay.java:313)
        at Assignment_1.jay.main(jay.java:125)
PS C:\Users\sizwe\Downloads\Assignment 1 Project\src> 
