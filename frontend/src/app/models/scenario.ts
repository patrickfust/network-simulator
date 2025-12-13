export interface Scenario {
  id: number;
  enableScenario: boolean;
  name: string;
  path: string;
  description: string;
  latencyMs: number;
  statusCode: number;
  responseBody : string;
  timeoutMs: number;
  followRedirect: boolean;

}
