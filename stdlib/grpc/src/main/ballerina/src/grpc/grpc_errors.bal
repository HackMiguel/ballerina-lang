// Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

# Holds the details of an gRPC error
#
# + message - Specific error message for the error
# + cause - Cause of the error; If this error occurred due to another error (Probably from another module)
public type Detail record {
    string message;
    error cause?;
};

# Identifies cancelled error.
const CANCELLED_ERROR = "{ballerina/grpc}CancelledError";
# Represents the operation canceled(typically by the caller) error.
public type CancelledError error<CANCELLED_ERROR, Detail>;

# Identifies unknown error.
const UNKNOWN_ERROR = "{ballerina/grpc}UnKnownError";
# Represents unknown error.(e.g. Status value received is unknown)
public type UnKnownError error<UNKNOWN_ERROR, Detail>;

# Identifies invalid argument error.
const INVALID_ARGUMENT_ERROR = "{ballerina/grpc}InvalidArgumentError";
# Represents client specified an invalid argument error.
public type InvalidArgumentError error<INVALID_ARGUMENT_ERROR, Detail>;

# Identifies deadline exceeded error.
const DEADLINE_EXCEEDED_ERROR = "{ballerina/grpc}DeadlineExceededError";
# Represents operation expired before completion error.
public type DeadlineExceededError error<DEADLINE_EXCEEDED_ERROR, Detail>;

# Identifies not found error.
const NOT_FOUND_ERROR = "{ballerina/grpc}NotFoundError";
# Represents requested entity (e.g., file or directory) not found error.
public type NotFoundError error<NOT_FOUND_ERROR, Detail>;

# Identifies already exists error.
const ALREADY_EXISTS_ERROR = "{ballerina/grpc}AleadyExistsError";
# Represents error occur when attempt to create an entity which already exists.
public type AleadyExistsError error<ALREADY_EXISTS_ERROR, Detail>;

# Identifies permission denied error.
const PERMISSION_DENIED_ERROR = "{ballerina/grpc}PermissionDeniedError";
# Represents error occur when the caller does not have permission to execute the specified operation.
public type PermissionDeniedError error<PERMISSION_DENIED_ERROR, Detail>;

# Identifies unauthenticated error
const UNAUTHENTICATED_ERROR = "{ballerina/grpc}UnauthenticatedError";
# Represents error occur when the request does not have valid authentication credentials for the operation.
public type UnauthenticatedError error<UNAUTHENTICATED_ERROR, Detail>;

# Identifies resource exhausted error.
const RESOURCE_EXHAUSTED_ERROR = "{ballerina/grpc}ResourceExhaustedError";
# Represents error occur when the resource is exhausted.
public type ResourceExhaustedError error<RESOURCE_EXHAUSTED_ERROR, Detail>;

# Identifies failed precondition error.
const FAILED_PRECONDITION_ERROR = "{ballerina/grpc}FailedPreconditionError";
# Represents error occur when operation is rejected because the system is not in a state required for the operation's execution.
public type FailedPreconditionError error<FAILED_PRECONDITION_ERROR, Detail>;

# Identifies aborted error.
const ABORTED_ERROR = "{ballerina/grpc}AbortedError";
# Represents error occur when operation is aborted.
public type AbortedError error<ABORTED_ERROR, Detail>;

# Identifies out of range error.
const OUT_OF_RANGE_ERROR = "{ballerina/grpc}OutOfRangeError";
# Represents error occur when specified value is out of range.
public type OutOfRangeError error<OUT_OF_RANGE_ERROR, Detail>;

# Identifies unimplemented error.
const UNIMPLEMENTED_ERROR = "{ballerina/grpc}UnimplementedError";
# Represents error occur when operation is not implemented or not supported/enabled in this service.
public type UnimplementedError error<UNIMPLEMENTED_ERROR, Detail>;

# Identifies internal error.
const INTERNAL_ERROR = "{ballerina/grpc}InternalError";
# Represents internal error.
public type InternalError error<INTERNAL_ERROR, Detail>;

# Identifies unavailable error.
const UNAVAILABLE_ERROR = "{ballerina/grpc}UnavailableError";
# Represents error occur when the service is currently unavailable.
public type UnavailableError error<UNAVAILABLE_ERROR, Detail>;

# Identifies data loss error.
const DATA_LOSS_ERROR = "{ballerina/grpc}DataLossError";
# Represents unrecoverable data loss or corruption erros.
public type DataLossError error<DATA_LOSS_ERROR, Detail>;

# Identifies all the retry attempts failed scenario.
const ALL_RETRY_ATTEMPTS_FAILED = "{ballerina/grpc}AllRetryAttemptsFailed";
# Represents error scenario where the maximum retry attempts are done and still received an error.
public type AllRetryAttemptsFailed error<ALL_RETRY_ATTEMPTS_FAILED, Detail>;

# Represents all the resiliency-related error reasons.
public type ResiliencyErrorType ALL_RETRY_ATTEMPTS_FAILED;
# Represents all the resiliency-related errors.
public type ResiliencyError AllRetryAttemptsFailed;

# Represents gRPC related error types.
public type ErrorType CANCELLED_ERROR | UNKNOWN_ERROR | INVALID_ARGUMENT_ERROR | DEADLINE_EXCEEDED_ERROR
| NOT_FOUND_ERROR | ALREADY_EXISTS_ERROR | PERMISSION_DENIED_ERROR | UNAUTHENTICATED_ERROR | RESOURCE_EXHAUSTED_ERROR
| FAILED_PRECONDITION_ERROR | ABORTED_ERROR | OUT_OF_RANGE_ERROR | UNIMPLEMENTED_ERROR |
INTERNAL_ERROR|UNAVAILABLE_ERROR | DATA_LOSS_ERROR | ResiliencyErrorType;

# Represents gRPC related errors.
public type Error CancelledError | UnKnownError | InvalidArgumentError | DeadlineExceededError | NotFoundError
| AleadyExistsError | PermissionDeniedError | UnauthenticatedError | ResourceExhaustedError | FailedPreconditionError
| AbortedError | OutOfRangeError | UnimplementedError | InternalError | UnavailableError | DataLossError
| ResiliencyError;


# Prepare the `error` as gRPC specific `Error`.
#
# + errorType - the error type.
# + message - the error message.
# + cause - the `error` instance.
# + return - prepared `grpc:Error` instance.
public function prepareError(ErrorType errorType, string message, error? cause) returns Error {
    if (cause is error) {
        error err = error(errorType, message = message, cause = cause);
        return <Error> err;
    } else {
        error err = error(errorType, message = message);
        return <Error> err;
    }
}
