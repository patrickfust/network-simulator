export interface TargetSystem {
  id?: number;
  systemName: string;
  targetBaseUrl: string;
  timeoutMs: number;
  followRedirect: boolean;
}
