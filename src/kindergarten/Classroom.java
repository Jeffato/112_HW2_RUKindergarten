package kindergarten;
/**
 * This class represents a Classroom, with:
 * - an SNode instance variable for students in line,
 * - an SNode instance variable for musical chairs, pointing to the last student in the list,
 * - a boolean array for seating availability (eg. can a student sit in a given seat), and
 * - a Student array parallel to seatingAvailability to show students filed into seats 
 * --- (more formally, seatingAvailability[i][j] also refers to the same seat in studentsSitting[i][j])
 * 
 * @author Ethan Chou
 * @author Kal Pandit
 * @author Maksims Kurjanovics Kravcenko
 */
public class Classroom {
    private SNode studentsInLine;             // when students are in line: references the FIRST student in the LL
    private SNode musicalChairs;              // when students are in musical chairs: references the LAST student in the CLL
    private boolean[][] seatingAvailability;  // represents the classroom seats that are available to students
    private Student[][] studentsSitting;      // when students are sitting in the classroom: contains the students

    /**
     * Constructor for classrooms. Do not edit.
     * @param l passes in students in line
     * @param m passes in musical chairs
     * @param a passes in availability
     * @param s passes in students sitting
     */
    public Classroom ( SNode l, SNode m, boolean[][] a, Student[][] s ) {
		studentsInLine      = l;
        musicalChairs       = m;
		seatingAvailability = a;
        studentsSitting     = s;
	}
    /**
     * Default constructor starts an empty classroom. Do not edit.
     */
    public Classroom() {
        this(null, null, null, null);
    }

    /**
     * This method simulates students coming into the classroom and standing in line.
     * 
     * Reads students from input file and inserts these students in alphabetical 
     * order to studentsInLine singly linked list.
     * 
     * Input file has:
     * 1) one line containing an integer representing the number of students in the file, say x
     * 2) x lines containing one student per line. Each line has the following student 
     * information separated by spaces: FirstName LastName Height
     * 
     * @param filename the student information input file
     */
    public void makeClassroom (String filename) {
        StdIn.setFile(filename);
        int lineLength = StdIn.readInt();

        for(int i = 0; i< lineLength; i++){
            String firstName = StdIn.readString();
            String lastName = StdIn.readString();
            int height = StdIn.readInt();

            Student s = new Student(firstName, lastName, height);
            insertStudentByAlpha(s);
        }
    }
    /*
        Insert Student s into studentsInLine in alphabetical order
     */
    private void insertStudentByAlpha(Student s){
        //EDGE: If studentsInLine is empty
        if(studentsInLine == null){
            studentsInLine = new SNode(s, null);
            return;
        }

        //DummyNode if node needs to be inserted at the front
        SNode dummyNode = new SNode(s, studentsInLine);
        SNode ptr = studentsInLine;
        SNode prevPtr = dummyNode;

        //Iterate through LL
        while(ptr != null){
            Student t = ptr.getStudent();

            //compareNameTo -> n < 0 means this student's name comes before parameter student
            if(s.compareNameTo(t) < 0){
                prevPtr.setNext(new SNode(s, ptr));
                studentsInLine = dummyNode.getNext();
                return;
            }

            prevPtr = ptr;
            ptr = ptr.getNext();
        }

        //Add to tail
        prevPtr.setNext(new SNode(s, null));
    }
    /**
     * 
     * This method creates and initializes the seatingAvailability (2D array) of 
     * available seats inside the classroom. Imagine that unavailable seats are broken and cannot be used.
     * 
     * Reads seating chart input file with the format:
     * An integer representing the number of rows in the classroom, say r
     * An integer representing the number of columns in the classroom, say c
     * Number of r lines, each containing c true or false values (true denotes an available seat)
     *  
     * This method also creates the studentsSitting array with the same number of
     * rows and columns as the seatingAvailability array
     * 
     * This method does not seat students on the seats.
     * 
     * @param seatingChart the seating chart input file
     */
    public void setupSeats(String seatingChart) {
        StdIn.setFile(seatingChart);
        int rows = StdIn.readInt();
        int cols = StdIn.readInt();

        seatingAvailability = new boolean[rows][cols];
        studentsSitting = new Student[rows][cols];

        for(int i = 0; i<rows; i++){
            for(int j = 0; j<cols; j++){
                seatingAvailability[i][j] = StdIn.readBoolean();
            }
        }
    }

    /**
     * 
     * This method simulates students taking their seats in the classroom.
     * 
     * 1. seats any remaining students from the musicalChairs starting from the front of the list
     * 2. starting from the front of the studentsInLine singly linked list
     * 3. removes one student at a time from the list and inserts them into studentsSitting according to
     *    seatingAvailability
     * 
     * studentsInLine will then be empty
     */
    public void seatStudents () {
	    //add musical chairs part later (1)
        if(musicalChairs != null){
            studentsInLine = new SNode(musicalChairs.getStudent(), studentsInLine);
            musicalChairs = null;
        }

        int row = 0;
        int col = 0;
        int numCols = seatingAvailability[0].length;

        //Seat all students in studentsSitting
        while(studentsInLine != null){
            Student s = studentsInLine.getStudent();
            boolean updated = false;

            //Do until student is seated
            while(!updated){
                int modCol = col % numCols;

                if(seatingAvailability[row][modCol]){
                    studentsSitting[row][modCol] = s;
                    updated = true;
                }

                col++;
                //Update row after cols are filled
                if(col % numCols == 0){
                    row++;
                }
            }

            studentsInLine = studentsInLine.getNext();
        }
    }

    /**
     * Traverses studentsSitting row-wise (starting at row 0) removing a seated
     * student and adding that student to the end of the musicalChairs list.
     * 
     * row-wise: starts at index [0][0] traverses the entire first row and then moves
     * into second row.
     */
    public void insertMusicalChairs () {
        SNode dummyNode = new SNode();
        SNode ptr = dummyNode;

        int numRows = studentsSitting.length;
        int numCols = studentsSitting[0].length;

        //Iterate through students sitting
        for(int i = 0; i <numRows; i++){
            for(int j = 0; j<numCols; j++){

                //Check if a student could be here, then if there actually is a student
                if(seatingAvailability[i][j] && studentsSitting[i][j] != null){
                    Student s = studentsSitting[i][j];
                    ptr.setNext(new SNode(s,null));
                    ptr = ptr.getNext();
                }
            }
        }

        //Create circularly linked list
        ptr.setNext(dummyNode.getNext());
        musicalChairs = ptr;
     }

    /**
     * 
     * This method repeatedly removes students from the musicalChairs until there is only one
     * student (the winner).
     * 
     * Choose a student to be elimnated from the musicalChairs using StdRandom.uniform(int b),
     * where b is the number of students in the musicalChairs. 0 is the first student in the 
     * list, b-1 is the last.
     * 
     * Removes eliminated student from the list and inserts students back in studentsInLine 
     * in ascending height order (shortest to tallest).
     * 
     * The last line of this method calls the seatStudents() method so that students can be seated.
     */
    public void playMusicalChairs() {
        //Find length of CLL
        int musicalChairsLength = 1;
        SNode ptr = musicalChairs.getNext();

        while(ptr != musicalChairs){
            musicalChairsLength++;
            ptr = ptr.getNext();
        }

        System.out.print("Length of MusicalChairs: " + musicalChairsLength);

        while(musicalChairsLength > 1) {
            ptr = musicalChairs;

            //Generate removal Index
            int removeIndex = StdRandom.uniform(musicalChairsLength);
            System.out.print(" Remove Student at: " + removeIndex);

            //Remove student at removal index
            for (int i = 0; i < removeIndex; i++) {
                ptr = ptr.getNext();
            }

            if(ptr.getNext() == musicalChairs){
                musicalChairs = ptr;
            }

            System.out.print(" Removed: " + ptr.getNext().getStudent().getFullName());
            Student removedStudent = ptr.getNext().getStudent();
            ptr.setNext(ptr.getNext().getNext());

            //Place eliminated student in studentsInLine by height order
            insertStudentByHeight(removedStudent);
            musicalChairsLength--;
            System.out.println();
            System.out.print("Length of MusicalChairs: " + musicalChairsLength);

        }

        //call seatStudents, but place person who won in the first position
        System.out.println(" Winner" + musicalChairs.getStudent().getFullName());
        seatStudents();
    }

    private void insertStudentByHeight(Student s){ //something is not right lmao
        //EDGE: If studentsInLine is empty
        if(studentsInLine == null){
            studentsInLine = new SNode(s, null);
            return;
        }

        //DummyNode if node needs to be inserted at the front
        SNode dummyNode = new SNode(s, studentsInLine);
        SNode ptr = studentsInLine;
        SNode prevPtr = dummyNode;

        //Iterate through LL
        while(ptr != null){
            Student t = ptr.getStudent();

            //Insert shortest to tallest
            if(s.getHeight() <= t.getHeight()){
                prevPtr.setNext(new SNode(s, ptr));
                studentsInLine = dummyNode.getNext();
//                System.out.println("hi");
                return;
            }

            //EDGE: Students have the same height -> Use order of insertion
            if(s.getHeight() == t.getHeight()){
                while(s.getHeight() == ptr.getStudent().getHeight()){
                    prevPtr = ptr;
                    ptr = ptr.getNext();
                }
//
//                if(ptr == null){
//                    prevPtr.setNext(new SNode(s, null));
//                    return;
//                }

                prevPtr.setNext(new SNode(s, ptr));
                studentsInLine = dummyNode.getNext();
//                System.out.println("hey");
                return;
            }

            prevPtr = ptr;
            ptr = ptr.getNext();
        }

        //Add to tail
        prevPtr.setNext(new SNode(s, null));
    }

    /**
     * Insert a student to wherever the students are at (ie. whatever activity is not empty)
     * Note: adds to the end of either linked list or the next available empty seat
     * @param firstName the first name
     * @param lastName the last name
     * @param height the height of the student
     */
    public void addLateStudent ( String firstName, String lastName, int height ) {
        Student lateStudent = new Student(firstName, lastName, height);

        //Add to last node of musical chairs
        if(musicalChairs != null){
            musicalChairs.setNext(new SNode(lateStudent, musicalChairs.getNext()));
            musicalChairs = musicalChairs.getNext();
        }

        //Add to last node of studentsInLine
        if(studentsInLine != null){
            SNode ptr = studentsInLine;

            while(ptr.getNext() != null){
                ptr = ptr.getNext();
            }

            ptr.setNext(new SNode(lateStudent, null));
        }

        //Add to first available seat
        int row = 0;
        int col = 0;
        int numCols = seatingAvailability[0].length;
        int numRows = seatingAvailability.length;

        //Seat all students in studentsSitting
        boolean updated = false;

        //Do until student is seated
        while(!updated && row < numRows){
            int modCol = col % numCols;

            if(seatingAvailability[row][modCol] && studentsSitting[row][modCol] == null){
                studentsSitting[row][modCol] = lateStudent;
                updated = true;
            }

            col++;
            //Update row after cols are filled
            if(col % numCols == 0){
                row++;
            }
        }
    }

    /**
     * A student decides to leave early
     * This method deletes an early-leaving student from wherever the students 
     * are at (ie. whatever activity is not empty)
     * 
     * Assume the student's name is unique
     * 
     * @param firstName the student's first name
     * @param lastName the student's last name
     */
    public void deleteLeavingStudent ( String firstName, String lastName ) {
        String modFirstName = cleanName(firstName);
        String modLastName = cleanName(lastName);
        boolean updated = false;

        if(musicalChairs != null){
            int musicalChairsLength = 1;
            SNode ptr = musicalChairs.getNext();

            while(ptr != musicalChairs){
                musicalChairsLength++;
                ptr = ptr.getNext();
            }

            ptr = musicalChairs;

            for(int i = 0; i<musicalChairsLength; i++){
                Student check = ptr.getNext().getStudent();
                if(check.getFirstName().equals(modFirstName) && check.getLastName().equals(modLastName)){

                    if(ptr.getNext() == musicalChairs){
                        musicalChairs = ptr;
                    }

                    ptr.setNext(ptr.getNext().getNext());
                    return;
                }

                ptr = ptr.getNext();
            }
        }

        if(studentsInLine != null){
            SNode dummyHead = new SNode();
            dummyHead.setNext(studentsInLine);
            SNode ptr= dummyHead;

            while(ptr.getNext() != null){
                Student check = ptr.getNext().getStudent();
                if(check.getFirstName().equals(modFirstName) && check.getLastName().equals(modLastName)){
                    ptr.setNext(ptr.getNext().getNext());

                    studentsInLine = dummyHead.getNext();
                    return;
                }

                ptr = ptr.getNext();
            }
            return;
        }

        else{
            int row = 0;
            int col = 0;
            int numCols = seatingAvailability[0].length;
            int numRows = seatingAvailability.length;

            while(!updated && row < numRows) {
                int modCol = col % numCols;

                if (studentsSitting[row][modCol] != null && studentsSitting[row][modCol].getFirstName().equals(modFirstName) && studentsSitting[row][modCol].getLastName().equals(modLastName)) {
                    studentsSitting[row][modCol] = null;
                    updated = true;
                }

                col++;
                //Update row after cols are filled
                if (col % numCols == 0) {
                    row++;
                }
            }
        }
    }

    private String cleanName(String name){
        String output = "";

        char[] temp = name.toCharArray();

        for(int i = 0; i<temp.length; i++){
            if(i == 0){
                output += Character.toUpperCase(temp[i]);
            }

            else{
                output += Character.toLowerCase(temp[i]);
            }
        }

        return output;
    }

    /**
     * Used by driver to display students in line
     * DO NOT edit.
     */
    public void printStudentsInLine () {

        //Print studentsInLine
        StdOut.println ( "Students in Line:" );
        if ( studentsInLine == null ) { StdOut.println("EMPTY"); }

        for ( SNode ptr = studentsInLine; ptr != null; ptr = ptr.getNext() ) {
            StdOut.print ( ptr.getStudent().print() );
            if ( ptr.getNext() != null ) { StdOut.print ( " -> " ); }
        }
        StdOut.println();
        StdOut.println();
    }

    /**
     * Prints the seated students; can use this method to debug.
     * DO NOT edit.
     */
    public void printSeatedStudents () {

        StdOut.println("Sitting Students:");

        if ( studentsSitting != null ) {
        
            for ( int i = 0; i < studentsSitting.length; i++ ) {
                for ( int j = 0; j < studentsSitting[i].length; j++ ) {

                    String stringToPrint = "";
                    if ( studentsSitting[i][j] == null ) {

                        if (seatingAvailability[i][j] == false) {stringToPrint = "X";}
                        else { stringToPrint = "EMPTY"; }

                    } else { stringToPrint = studentsSitting[i][j].print();}

                    StdOut.print ( stringToPrint );
                    
                    for ( int o = 0; o < (10 - stringToPrint.length()); o++ ) {
                        StdOut.print (" ");
                    }
                }
                StdOut.println();
            }
        } else {
            StdOut.println("EMPTY");
        }
        StdOut.println();
    }

    /**
     * Prints the musical chairs; can use this method to debug.
     * DO NOT edit.
     */
    public void printMusicalChairs () {
        StdOut.println ( "Students in Musical Chairs:" );

        if ( musicalChairs == null ) {
            StdOut.println("EMPTY");
            StdOut.println();
            return;
        }
        SNode ptr;
        for ( ptr = musicalChairs.getNext(); ptr != musicalChairs; ptr = ptr.getNext() ) {
            StdOut.print(ptr.getStudent().print() + " -> ");
        }
        if ( ptr == musicalChairs) {
            StdOut.print(musicalChairs.getStudent().print() + " - POINTS TO FRONT");
        }
        StdOut.println();
    }

    /**
     * Prints the state of the classroom; can use this method to debug.
     * DO NOT edit.
     */
    public void printClassroom() {
        printStudentsInLine();
        printSeatedStudents();
        printMusicalChairs();
    }

    /**
     * Used to get and set objects.
     * DO NOT edit.
     */

    public SNode getStudentsInLine() { return studentsInLine; }
    public void setStudentsInLine(SNode l) { studentsInLine = l; }

    public SNode getMusicalChairs() { return musicalChairs; }
    public void setMusicalChairs(SNode m) { musicalChairs = m; }

    public boolean[][] getSeatingAvailability() { return seatingAvailability; }
    public void setSeatingAvailability(boolean[][] a) { seatingAvailability = a; }

    public Student[][] getStudentsSitting() { return studentsSitting; }
    public void setStudentsSitting(Student[][] s) { studentsSitting = s; }

}
