# cfn-lambda

A [Lambda](https://aws.amazon.com/lambda/) function in Scala with 
[CloudFormation](https://aws.amazon.com/cloudformation/) templates that deploy the function and 
trigger redeployments on version control updates.

## Prerequisites

Save your GitHub access token as a SecretString under key `dev/github/token` in 
[AWS Secrets Manager](https://aws.amazon.com/secrets-manager/).

## Usage

1. Deploy CloudFormation template [lambda-pipeline.cfn.yml](lambda-pipeline.cfn.yml).
