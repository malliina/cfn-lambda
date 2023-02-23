# https://docs.aws.amazon.com/cdk/api/v1/docs/pipelines-readme.html#migrating-from-buildspecyml-files
echo "Building function to $OUTPUT_DIR"
sbt "project lambda" assembly
unzip lambda/function.jar -d jartarget
echo "Running synth with CDK $CDK_VERSION"
npm install -g aws-cdk
cdk synth
