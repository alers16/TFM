// @caseId IOTBROKER_AMQP_CLIENT_PROCESS_S_A_S_L_OUTCOME
// @origin iotbroker
// @project iotbroker
// @file AmqpClient.java
// @method processSASLOutcome(byte[], OutcomeCode)
// @license [PENDIENTE]
// @sonarCCBefore 3
// @sonarCCAfter 2   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class IotbrokerAmqpClientProcessSASLOutcome {
    public void processSASLOutcome(byte[] additionalData, OutcomeCode outcomeCode) {
        if (outcomeCode != null)
            if (outcomeCode == OutcomeCode.OK) {
                isSaslConfirm = true;
                AMQPProtoHeader header = new AMQPProtoHeader(0);
                client.send(header);
            }
    }
}
