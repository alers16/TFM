// @caseId TUTOR_JMETAL_EBES_READ_DATA_FILE
// @origin tutor-test-corpus
// @project jMetal (test resource of Saborido et al. plug-in)
// @file Ebes.java
// @method EBEsReadDataFile(String)
// @license LGPL-3.0-or-later
// @sonarCCBefore 5059
// @description Parser de fichero de definicion EBE; metodo diana del corpus de Saborido et al. 2022.
// @upstream Saborido et al. 2022, IEEE Access, doi:10.1109/ACCESS.2022.3144743 (test resource).

class TutorJmetalEbesReadDataFile {




  public final void EBEsReadDataFile(String fileName) throws JMetalException{

    int i, j=0;
    char ch;
    String txt = "";

    try {
      // create a File instance
      InputStream inputStream = getClass().getResourceAsStream("/" + fileName);

      if (inputStream == null) {
        inputStream = new FileInputStream(fileName);
      }
      // create a Scanner for the file
      Scanner input = new Scanner(inputStream);
      // Read data from file
      while(input.hasNext()){
        for (i=0;i<5;i++){txt=input.nextLine();}

        // number of nodes
        for(i=txt.length()-1;i>=0;i--){
          ch=txt.charAt(i);
          if(ch == ' '){
            j=i+1;break;}
        }
        txt = txt.substring(j);
        numberOfNodes =Integer.valueOf(txt);

        // number of restriction
        txt=input.nextLine();
        for(i=txt.length()-1;i>=0;i--){
          ch=txt.charAt(i);
          if(ch == ' '){
            j=i+1;break;}
        }
        txt = txt.substring(j);
        numberOfNodesRestricts_=Integer.valueOf(txt);

        // number of bar groups
        txt=input.nextLine();
        for(i=txt.length()-1;i>=0;i--){
          ch=txt.charAt(i);
          if(ch == ' '){
            j=i+1;break;}
        }
        txt = txt.substring(j);
        numberOfGroupElements_=Integer.valueOf(txt);

        // number of elements
        txt=input.nextLine();
        for(i=txt.length()-1;i>=0;i--){
          ch=txt.charAt(i);
          if(ch == ' '){
            j=i+1;break;}
        }
        txt = txt.substring(j);
        numberOfElements_=Integer.valueOf(txt);

        // number of hipotesis
        for (i=0;i<5;i++){txt=input.nextLine();}
        for(i=txt.length()-1;i>=0;i--){
          ch=txt.charAt(i);
          if(ch == ' '){
            j=i+1;break;}
        }
        txt = txt.substring(j);
        numberOfWeigthHypothesis_=Integer.valueOf(txt);
        numberOfWeigthHypothesis_=1;

        // load as own weight for elements
        txt=input.nextLine();
        for(i=txt.length()-1;i>=0;i--){
          ch=txt.charAt(i);
          if(ch == ' '){
            j=i+1;break;}
        }
        txt = txt.substring(j);
        lLoadsOwnWeight =Boolean.valueOf(txt);

        // Weight elements
        txt=input.nextLine();
        for(i=txt.length()-1;i>=0;i--){
          ch=txt.charAt(i);
          if(ch == ' '){
            j=i+1;break;}
        }
        txt = txt.substring(j);
        numberOfWeigthsElements_=Integer.valueOf(txt);

        // Weight nodes
        txt=input.nextLine();
        for(i=txt.length()-1;i>=0;i--){
          ch=txt.charAt(i);
          if(ch == ' '){
            j=i+1;break;}
        }
        txt = txt.substring(j);
        numberOfWeigthsNodes_=Integer.valueOf(txt);
        //txt = input.nextLine();
        //txt = input.next();

        // read lines
        for (i=0;i<4;i++){txt=input.nextLine();}

        // check node constraint
        for(i=txt.length()-1;i>=0;i--){
          ch=txt.charAt(i);
          if(ch == ' '){
            j=i+1;break;}
        }
        txt = txt.substring(j);
        numberOfConstraintsNodes_ = Integer.valueOf(txt);

        // number Of Groups To Check Geometry
        // read lines
        txt=input.nextLine();
        for(i=txt.length()-1;i>=0;i--){
          ch=txt.charAt(i);
          if(ch == ' '){
            j=i+1;break;}
        }
        txt = txt.substring(j);
        numberOfGroupsToCheckGeometry_= Integer.valueOf(txt);

        // Cutting efect (not not included, read lines)
        txt=input.nextLine();

        // considered second-order effect
        txt=input.nextLine();
        for(i=txt.length()-1;i>=0;i--){
          ch=txt.charAt(i);
          if(ch == ' '){
            j=i+1;break;}
        }
        txt = txt.substring(j);
        lSecondOrderGeometric =Boolean.valueOf(txt);

        // considered buckling effect
        txt=input.nextLine();
        for(i=txt.length()-1;i>=0;i--){
          ch=txt.charAt(i);
          if(ch == ' '){
            j=i+1;break;}
        }
        txt = txt.substring(j);
        lBuckling =Boolean.valueOf(txt);

        // read lines
        for (i=0;i<3;i++){txt=input.nextLine();}

        Node_ = new double[numberOfNodes][4];
        for (i=0;i< numberOfNodes;i++){
          txt=input.next();
          for (j=0;j<4;j++){
            Node_[i][j]=Double.valueOf(input.next());
          }
          for (j=0;j<6;j++){txt=input.next();}
        }

        NodeRestrict_ = new double[numberOfNodesRestricts_][2];
        j=0;
        for(i=0;i< numberOfNodes;i++){
          if(Node_[i][3] != 0){
            // Restriction of the movement
            NodeRestrict_[j][0]=i;
            NodeRestrict_[j][1]=Node_[i][3];
            j++;
          }
        }
        // ELEMENTS GROUPS
        txt=input.nextLine();
        txt=input.nextLine();
        Groups_ = new double[numberOfGroupElements_][MAX_COLUMN];
        for (i=0;i<numberOfGroupElements_;i++){
          for (j=0;j<MAX_COLUMN-1;j++){
            Groups_[i][j]=Double.valueOf(input.next());
          }
          input.next(); // description
        }

        // ELEMENTS
        txt=input.nextLine();
        txt=input.nextLine();
        Element_ = new double[numberOfElements_][8];
        for (i=0;i<numberOfElements_;i++){
          txt=input.next();// BARRAS
          Element_[i][INDEX_]=Double.valueOf(input.next());
          Element_[i][i_]=Double.valueOf(input.next());
          Element_[i][j_]=Double.valueOf(input.next());
          Element_[i][L_]=Double.valueOf(input.next());
          Element_[i][Vij_]=Double.valueOf(input.next());
          Element_[i][Ei_]=Double.valueOf(input.next());
          Element_[i][Ej_]=Double.valueOf(input.next());
          // correction
          int ni = (int)Element_[i][i_];
          int nj = (int)Element_[i][j_];
          double xi, yi, zi;
          double xj, yj, zj;
          //coordenadas de los extremso de la barra
                /*
                if(Math.abs(Node_[ni][aX_])<= 0.000001)
                   xi = 0.0;
                else xi=Node_[ni][aX_];

                if(Math.abs(Node_[ni][aY_])<= 0.000001)
                	yi= 0.0;
                else yi = Node_[ni][aY_];

                if(Math.abs(Node_[ni][aZ_])<= 0.000001)
                    zi = 0.0;
                else zi = Node_[ni][aZ_];

                if(Math.abs(Node_[nj][aX_])<= 0.000001)
                    xj = 0.0;
                else xj=Node_[nj][aX_];

                if(Math.abs(Node_[nj][aY_])<= 0.000001)
                	yj=0.0;
                else yj = Node_[nj][aY_];

                if(Math.abs(Node_[nj][aZ_])<= 0.000001)
                	zj = 0.0;
                else
                */
          xi = Node_[ni][aX_];
          yi = Node_[ni][aY_];
          zi = Node_[ni][aZ_];
          xj = Node_[nj][aX_];
          yj = Node_[nj][aY_];
          zj = Node_[nj][aZ_];
          Element_[i][L_]=Math.sqrt(Math.pow((xj - xi), 2.0) + Math.pow((yj - yi), 2.0) + Math.pow((zj - zi), 2.0));
          if(Element_[i][L_] < 0.001) Element_[i][L_] = 0.0;
        }
        txt=input.nextLine();
        txt=input.nextLine();
        // OVERLOAD
        OverloadInElement_ = new double[numberOfWeigthsElements_][8];
        for (i=0;i<numberOfWeigthsElements_;i++){
          txt=input.next(); // load number
          for (j=0;j<8;j++){
            OverloadInElement_[i][j]=Double.valueOf(input.next());
          }
        }

        // LOAD NODES
        txt=input.nextLine();
        if(numberOfWeigthsElements_!=0){txt=input.nextLine();}
        WeightNode_ = new double[numberOfWeigthsNodes_][8];
        for (i=0;i<numberOfWeigthsNodes_;i++){
          txt=input.next();
          for (j=0;j<8;j++){
            WeightNode_[i][j]=Double.valueOf(input.next());
          }
        }

        // CHECK NODE FOR DISPLACEMENT (CONSTRAINT)
        txt=input.nextLine();
        txt=input.nextLine();
        txt=input.nextLine();
        txt=input.nextLine();
        if(numberOfWeigthsNodes_!=0){txt=input.nextLine();}
        nodeCheck_ = new double[numberOfConstraintsNodes_][2];
        for (i=0;i<numberOfConstraintsNodes_;i++){
          nodeCheck_[i][0]=Double.valueOf(input.next());
          nodeCheck_[i][1]=Double.valueOf(input.next());
        }

        //number of groups to check geometry
        txt=input.nextLine();
        txt=input.nextLine();
        if(numberOfGroupsToCheckGeometry_!=0) {
          geometryCheck_ = new int[numberOfGroupsToCheckGeometry_][];
          //txt = input.nextLine();
          for (i = 0; i < numberOfGroupsToCheckGeometry_; i++) {
            txt = input.nextLine();
            geometryCheck_[i] = new int[(txt.length() + 1) / 2];
            String aTxt[] = txt.split(" ");
            int k = 0;
            for (j = 0; j < aTxt.length; j++) {
              if (aTxt[j] != " ") {
                geometryCheck_[i][k] =  Integer.parseInt(aTxt[j]);
                k++;
              }
            }
          }
        }
        while(input.hasNext()){
          txt=input.nextLine();
        }
      }

      // clse the file
      input.close();
    }
    catch (Exception ex) {
      System.out.println("Error: data file EBEs not readed");
      System.out.println(ex.getMessage());
      System.exit(1);
    }
  }
}
