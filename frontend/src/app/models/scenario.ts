import {TargetSystem} from './target-system';

export interface ScenarioHeader {
  id?: number;
  headerName: string;
  headerValue: string;
  headerReplaceValue: boolean;
}

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
  headers: ScenarioHeader[];
  targetSystemId?: number;
  responseBytesPerSecond?: number;
}
