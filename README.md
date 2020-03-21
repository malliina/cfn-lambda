# cfn-lambda

[AWS Lambda](https://aws.amazon.com/lambda/) functions in Scala with 
[CloudFormation](https://aws.amazon.com/cloudformation/) templates that redeploy the functions on version control 
updates.

See https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/sam-specification.html for details.

## Prerequisites

Save your GitHub access token as a SecretString under key `dev/github/token` in 
[AWS Secrets Manager](https://aws.amazon.com/secrets-manager/).

## Usage

Deploy CloudFormation template [lambda-pipeline.cfn.yml](lambda-pipeline.cfn.yml).

You must choose one of the following input templates as a parameter to the template:

| Input template | What it does
| -------------- | --------------
| [input-scheduled.cfn.yml](input-scheduled.cfn.yml) | Triggers the Lambda based on an optional cron-style schedule
| [input-s3.cfn.yml](input-s3.cfn.yml) | Triggers the Lambda based S3 event notifications, for example when a file is uploaded to S3

Optional: Point the `LambdaConfiguration` parameter to a configuration file with custom parameters. The parameters in 
the file will be exposed as environment variables to the Lambda. This can be useful if you deploy multiple instances of 
the Lambda with different configuration parameters. An example [lambda-configuration.json](lambda-configuration.json) 
file is provided.

### Deleting the stack

Deleting the CloudFormation stack might fail due to some problem with roles. If that happens, try 
this (from https://stackoverflow.com/a/53886359):

1. Create a role that uses service CloudFormation and attach the following permissions policies to 
 the role:
    - AWSCloudFormationFullAccess
    - AWSLambdaFullAccess
    - IAMFullAccess
1. Create a change set of the stack you want to delete
1. Change the IAM role in the change set to the one you created in step 1
1. Execute the change set
1. Delete the stack, this time successfully

If that fails:

1. Prepare your AWS CLI with the profile and region of your preference
1. Assuming the ARN of the role is *arn:aws:iam::xxx:role/CloudFormationDeleter* and stack name 
*doomed-stack*, delete the stack using the role created in step 1:

        aws cloudformation delete-stack --role-arn arn:aws:iam::xxx:role/CloudFormationDeleter --stack-name doomed-stack
