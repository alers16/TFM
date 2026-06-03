public class IOTBROKER_AMQP_CLIENT_PROCESS_S_A_S_L_OUTCOME {
public void processSASLOutcome(byte[] additionalData, OutcomeCode outcomeCode) {
    if (outcomeCode != null && outcomeCode == OutcomeCode.OK) {
        isSaslConfirm = true;
        AMQPProtoHeader header = new AMQPProtoHeader(0);
        client.send(header);
    }
}
}

