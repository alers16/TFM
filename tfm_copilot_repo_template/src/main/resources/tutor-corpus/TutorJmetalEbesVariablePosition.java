// @caseId TUTOR_JMETAL_EBES_VARIABLE_POSITION
// @origin tutor-test-corpus
// @project jMetal (test resource of Saborido et al. plug-in)
// @file Ebes.java
// @method Variable_Position()
// @license LGPL-3.0-or-later
// @sonarCCBefore 5360
// @description Calculo de posicion de variables; metodo diana del corpus de Saborido et al. 2022.
// @upstream Saborido et al. 2022, IEEE Access, doi:10.1109/ACCESS.2022.3144743 (test resource).

class TutorJmetalEbesVariablePosition {



  public int Variable_Position() {
    int numberOfVariables_ = 0;
    try{
      numberOfConstraintsGeometric_ = 0;
      for (int gr = 0; gr < numberOfGroupElements_; gr++) {
        if (Groups_[gr][SHAPE] == CIRCLE) {
          // variables
          numberOfVariables_ += 1;
          Groups_[gr][VARIABLES] = 1;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 1;
          // constrain
          numberOfConstraintsGeometric_ += 0;
          Groups_[gr][CONSTRAINT]=0;

        } else if (Groups_[gr][SHAPE] == HOLE_CIRCLE) {
          // variables
          numberOfVariables_ += 2;
          Groups_[gr][VARIABLES] = 2;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 2;
          // constrain
          numberOfConstraintsGeometric_ += 2;
          Groups_[gr][CONSTRAINT]=2;

        } else if (Groups_[gr][SHAPE] == RECTANGLE) {
          // variables
          numberOfVariables_ += 2;
          Groups_[gr][VARIABLES] = 2;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 2;
          // constrain
          numberOfConstraintsGeometric_ += 2;
          Groups_[gr][CONSTRAINT]=2;

        } else if (Groups_[gr][SHAPE] == HOLE_RECTANGLE) {
          // variable
          numberOfVariables_ += 4;
          Groups_[gr][VARIABLES] = 4;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
          // constrain
          numberOfConstraintsGeometric_ += 4;
          Groups_[gr][CONSTRAINT]=4;

        } else if (Groups_[gr][SHAPE] == I_SINGLE) {
          // variable
          numberOfVariables_ += 4;
          Groups_[gr][VARIABLES] = 4;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
          // constrain
          numberOfConstraintsGeometric_ += 4;
          Groups_[gr][CONSTRAINT]=4;

        } else if (Groups_[gr][SHAPE] == I_DOUBLE) {
          // variable
          numberOfVariables_ += 4;
          Groups_[gr][VARIABLES] = 4;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
          // constrain
          numberOfConstraintsGeometric_ += 4;
          Groups_[gr][CONSTRAINT]=4;

        } else if (Groups_[gr][SHAPE] == H_SINGLE) {
          // variables
          numberOfVariables_ += 4;
          Groups_[gr][VARIABLES] = 4;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
          // constrain
          numberOfConstraintsGeometric_ += 4;
          Groups_[gr][CONSTRAINT]=4;

        } else if (Groups_[gr][SHAPE] == H_DOUBLE) {
          // variable
          numberOfVariables_ += 4;
          Groups_[gr][VARIABLES] = 4;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
          // constrain
          numberOfConstraintsGeometric_ += 4;
          Groups_[gr][CONSTRAINT]=4;

        } else if (Groups_[gr][SHAPE] == L_SINGLE) {
          // variable
          numberOfVariables_ += 4;
          Groups_[gr][VARIABLES] = 4;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
          // constrain
          numberOfConstraintsGeometric_ += 4;
          Groups_[gr][CONSTRAINT]=4;

        } else if (Groups_[gr][SHAPE] == L_DOUBLE) {
          // variable
          numberOfVariables_ += 4;
          Groups_[gr][VARIABLES] = 4;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
          // constrain
          numberOfConstraintsGeometric_ += 4;
          Groups_[gr][CONSTRAINT]=4;

        } else if (Groups_[gr][SHAPE] == T_SINGLE) {
          // variable
          numberOfVariables_ += 4;
          Groups_[gr][VARIABLES] = 4;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
          // constrain
          numberOfConstraintsGeometric_ += 4;
          Groups_[gr][CONSTRAINT]=4;

        } else if (Groups_[gr][SHAPE] == T_DOUBLE) {
          // variale
          numberOfVariables_ += 4;
          Groups_[gr][VARIABLES] = 4;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
          // constrain
          numberOfConstraintsGeometric_ += 4;
          Groups_[gr][CONSTRAINT]=4;

        } else {
          System.out.println("Error: transversal section not considerated in " + gr + " group");
          System.exit(1);
        } // end if
      } // gr
    }
    catch (Exception ex) {
      System.out.println(ex.getCause());
      System.out.println(ex.getMessage());
      System.exit(1);
    }

    return numberOfVariables_ ;
  }
}
