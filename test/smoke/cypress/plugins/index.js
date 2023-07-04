module.exports = (on, config) => {
  config.env.nexusPass = process.env.NEXUS_PASS;
  config.env.checkmarxToken = process.env.TEST_checkmarx_TOKEN;
  config.env.checkmarxOrg = process.env.TEST_checkmarx_ORG;
  return config;
}
