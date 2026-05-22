// @caseId TUTOR_CYBERCAPTOR_VERTEX_GET_RELATED_MACHINE
// @origin tutor-paper-corpus
// @project fiware-cybercaptor/cybercaptor-server (Saborido et al. 2022, 10 proyectos)
// @file org/fiware/cybercaptor/server/attackgraph/Vertex.java
// @method getRelatedMachine(InformationSystem)
// @license Apache-2.0
// @sonarCCBefore 52
// @description Vertex de grafo de ataque; busca la maquina referenciada segun el tipo de hecho DATALOG.
//              Patron elegible: if (this.fact != null) { if (this.fact.type == DATALOG && datalogCommand != null) { ... } }
// @upstream Saborido et al. 2022, IEEE Access, doi:10.1109/ACCESS.2022.3144743 (proyecto de evaluacion).
// @sourceCommit b6b1f10

import java.util.List;

class TutorCybercaptorVertexGetRelatedMachine {

    public InformationSystemHost getRelatedMachine(InformationSystem informationSystem) throws Exception {
        if (concernedMachine != null)
            return concernedMachine;
        InformationSystemHost result = null;
        if (this.fact != null) {
            if (this.fact.type == Fact.FactType.DATALOG_FACT && this.fact.datalogCommand != null) {
                DatalogCommand command = this.fact.datalogCommand;
                switch (command.command) {
                    case "vulExists":
                        if (command.params.length >= 1) {
                            result = informationSystem.existingMachineByNameOrIPAddress(command.params[0]);
                        }
                        break;
                    case "execCode":
                        if (command.params.length >= 1) {
                            result = informationSystem.existingMachineByNameOrIPAddress(command.params[0]);
                        }
                        break;
                    case "netAccess":
                        if (command.params.length >= 1) {
                            result = informationSystem.existingMachineByNameOrIPAddress(command.params[0]);
                        }
                        break;
                    case "canAccessHost":
                        if (command.params.length >= 1) {
                            result = informationSystem.existingMachineByNameOrIPAddress(command.params[0]);
                        }
                        break;
                    case "hacl":
                        if (command.params.length >= 2) {
                            result = informationSystem.existingMachineByNameOrIPAddress(command.params[1]);
                        }
                        break;
                    case "accessMaliciousInput":
                        if (command.params.length >= 1) {
                            result = informationSystem.existingMachineByNameOrIPAddress(command.params[0]);
                        }
                        break;
                    case "networkServiceInfo":
                        if (command.params.length >= 1) {
                            result = informationSystem.existingMachineByNameOrIPAddress(command.params[0]);
                        }
                        break;
                    case "principalCompromised":
                        if (command.params.length >= 1) {
                            result = informationSystem.existingMachineByUserName(command.params[0]);
                        }
                        break;
                    case "attackerLocated":
                        if (command.params.length >= 1) {
                            result = informationSystem.existingMachineByNameOrIPAddress(command.params[0]);
                        }
                        break;
                    case "accessFile":
                        if (command.params.length >= 1) {
                            result = informationSystem.existingMachineByNameOrIPAddress(command.params[0]);
                        }
                        break;
                    case "hasAccount":
                        if (command.params.length >= 1) {
                            result = informationSystem.existingMachineByNameOrIPAddress(command.params[1]);
                        }
                        break;
                }
            }
        }
        this.concernedMachine = result;
        return result;
    }
}
