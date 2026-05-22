// @caseId CYBERCAPTOR_VERTEX_GET_RELATED_MACHINE
// @origin cybercaptor
// @project cybercaptor
// @file Vertex.java
// @method getRelatedMachine(InformationSystem)
// @license [PENDIENTE]
// @sonarCCBefore 52
// @sonarCCAfter 38   (estimado; CC delta=-14)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class CybercaptorVertexGetRelatedMachine {
    /**
     * Get the machine which is referenced by this vertex of the attack graph
     * @param informationSystem the information system in which is the machine to find
     * @return the machine of the information system referenced by this vertex
     * @throws Exception
     */
    public InformationSystemHost getRelatedMachine(InformationSystem informationSystem) throws Exception {
        if (concernedMachine != null)
            return concernedMachine;
        InformationSystemHost result = null;
        if (this.fact != null) {
            if (this.fact.type == Fact.FactType.DATALOG_FACT && this.fact.datalogCommand != null) {
                DatalogCommand command = this.fact.datalogCommand;
                switch(command.command) {
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
