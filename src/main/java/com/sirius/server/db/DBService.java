package com.sirius.server.db;

import com.sirius.server.Msg;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class DBService extends DBServiceGrpc.DBServiceImplBase{
    @Override
    public void sql(Msg.DBRequest request, StreamObserver<Msg.DBResponse> responseObserver) {
        responseObserver.onNext(null);
        responseObserver.onCompleted();
    }
}
