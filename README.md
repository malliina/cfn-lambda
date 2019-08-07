# cfn-lambda

A [Lambda](https://aws.amazon.com/lambda/) function in Scala with 
[CloudFormation](https://aws.amazon.com/cloudformation/) templates that deploy the function and 
trigger redeployments on version control updates.

## Prerequisites

Save your GitHub access token as a SecretString under key `dev/github/token` in 
[AWS Secrets Manager](https://aws.amazon.com/secrets-manager/).

## Usage

Deploy CloudFormation template [lambda-pipeline.cfn.yml](lambda-pipeline.cfn.yml).

### Deleting the stack

Deleting the CloudFormation stack might fail due to some problem with roles. If that happens, try 
this (from https://stackoverflow.com/a/48821876):

1. Create a role that uses service CloudFormation and attach the following permissions policies to 
 the role:
    - AWSCloudFormationFullAccess
    - AWSLambdaFullAccess
    - IAMFullAccess
1. Prepare your AWS CLI with the profile and region of your preference
1. Assuming the ARN of the role is *arn:aws:iam::xxx:role/CloudFormationDeleter* and stack name 
*doomed-stack*, delete the stack using the role created in step 1:

        aws cloudformation delete-stack --role-arn arn:aws:iam::xxx:role/CloudFormationDeleter --stack-name doomed-stack
