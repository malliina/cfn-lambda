# cfn-lambda

- Scala
- CDK
- AWS CodeCommit
- AWS CodePipeline
- AWS Lambda

## Usage

    cdk deploy lambda-pipe
    git remote add aws codecommit::eu-west-1://lambda-pipe
    git push aws

This triggers CodePipeline to build the Lambda function code and deploy it to a Lambda stack.

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
