# https://docs.aws.amazon.com/cdk/api/v1/docs/pipelines-readme.html#migrating-from-buildspecyml-files
sbt "project lambda" assembly
unzip lambda/function.jar -d jartarget
npm install -g aws-cdk
cdk synth
