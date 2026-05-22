// @caseId TUTOR_IOTBROKER_AMQP_PROCESS_SASL_OUTCOME
// @origin tutor-paper-corpus
// @project mobius-software-ltd/iotbroker.cloud-java-client (Saborido et al. 2022, 10 proyectos)
// @file com/mobiussoftware/iotbroker/amqp/AmqpClient.java
// @method processSASLOutcome(byte[], OutcomeCode)
// @license Apache-2.0
// @sonarCCBefore 3
// @description Procesador de resultado SASL en cliente AMQP; patron elegible con if anidado sin llaves:
//              if (outcomeCode != null) if (outcomeCode == OutcomeCode.OK) { ... }
// @upstream Saborido et al. 2022, IEEE Access, doi:10.1109/ACCESS.2022.3144743 (proyecto de evaluacion).
// @sourceCommit 98eeceb

class TutorIotbrokerAmqpProcessSaslOutcome {

    public void processSASLOutcome(byte[] additionalData, OutcomeCode outcomeCode) {
        if (outcomeCode != null)
            if (outcomeCode == OutcomeCode.OK) {
                isSaslConfirm = true;
                AMQPProtoHeader header = new AMQPProtoHeader(0);
                client.send(header);
            }
    }
}
