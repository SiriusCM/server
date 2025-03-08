package com.sirius.server.db;

import com.sirius.server.msg.Msg;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * 定义服务
 * </pre>
 */
@javax.annotation.processing.Generated(
    value = "by gRPC proto compiler (version 1.69.0)",
    comments = "Source: msg.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class DBServiceGrpc {

  private DBServiceGrpc() {}

  public static final String SERVICE_NAME = "com.sirius.game.db.DBService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<Msg.DBRequest,
      Msg.DBResponse> getSqlMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "sql",
      requestType = Msg.DBRequest.class,
      responseType = Msg.DBResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<Msg.DBRequest,
      Msg.DBResponse> getSqlMethod() {
    io.grpc.MethodDescriptor<Msg.DBRequest, Msg.DBResponse> getSqlMethod;
    if ((getSqlMethod = DBServiceGrpc.getSqlMethod) == null) {
      synchronized (DBServiceGrpc.class) {
        if ((getSqlMethod = DBServiceGrpc.getSqlMethod) == null) {
          DBServiceGrpc.getSqlMethod = getSqlMethod =
              io.grpc.MethodDescriptor.<Msg.DBRequest, Msg.DBResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "sql"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Msg.DBRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Msg.DBResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DBServiceMethodDescriptorSupplier("sql"))
              .build();
        }
      }
    }
    return getSqlMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DBServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DBServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DBServiceStub>() {
        @Override
        public DBServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DBServiceStub(channel, callOptions);
        }
      };
    return DBServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DBServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DBServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DBServiceBlockingStub>() {
        @Override
        public DBServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DBServiceBlockingStub(channel, callOptions);
        }
      };
    return DBServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DBServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DBServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DBServiceFutureStub>() {
        @Override
        public DBServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DBServiceFutureStub(channel, callOptions);
        }
      };
    return DBServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * 定义服务
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * 定义服务方法
     * </pre>
     */
    default void sql(Msg.DBRequest request,
                     io.grpc.stub.StreamObserver<Msg.DBResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSqlMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service DBService.
   * <pre>
   * 定义服务
   * </pre>
   */
  public static abstract class DBServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return DBServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service DBService.
   * <pre>
   * 定义服务
   * </pre>
   */
  public static final class DBServiceStub
      extends io.grpc.stub.AbstractAsyncStub<DBServiceStub> {
    private DBServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected DBServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DBServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * 定义服务方法
     * </pre>
     */
    public void sql(Msg.DBRequest request,
                    io.grpc.stub.StreamObserver<Msg.DBResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSqlMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service DBService.
   * <pre>
   * 定义服务
   * </pre>
   */
  public static final class DBServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<DBServiceBlockingStub> {
    private DBServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected DBServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DBServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 定义服务方法
     * </pre>
     */
    public Msg.DBResponse sql(Msg.DBRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSqlMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service DBService.
   * <pre>
   * 定义服务
   * </pre>
   */
  public static final class DBServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<DBServiceFutureStub> {
    private DBServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected DBServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DBServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 定义服务方法
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<Msg.DBResponse> sql(
        Msg.DBRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSqlMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SQL = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SQL:
          serviceImpl.sql((Msg.DBRequest) request,
              (io.grpc.stub.StreamObserver<Msg.DBResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getSqlMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              Msg.DBRequest,
              Msg.DBResponse>(
                service, METHODID_SQL)))
        .build();
  }

  private static abstract class DBServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DBServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Msg.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DBService");
    }
  }

  private static final class DBServiceFileDescriptorSupplier
      extends DBServiceBaseDescriptorSupplier {
    DBServiceFileDescriptorSupplier() {}
  }

  private static final class DBServiceMethodDescriptorSupplier
      extends DBServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DBServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (DBServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DBServiceFileDescriptorSupplier())
              .addMethod(getSqlMethod())
              .build();
        }
      }
    }
    return result;
  }
}
