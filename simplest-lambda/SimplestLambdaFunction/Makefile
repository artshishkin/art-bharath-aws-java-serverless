build-SimplestLambdaFunction:
	mvn clean test
	mvn dependency:copy-dependencies -DexcludeScope=provided -DoutputDirectory=$(ARTIFACTS_DIR)/lib
# 	cp -rf ./target/* $(ARTIFACTS_DIR)/
# 	cp -rf ./target/dependency/* $(ARTIFACTS_DIR)/lib/
	cp -rf ./target/classes/* $(ARTIFACTS_DIR)/
