import { NativeModules } from 'react-native';

type QiyuType = {
  multiply(a: number, b: number): Promise<number>;
  registerAppId(appKey: string, appName: string): Promise<number>;
  openServiceWindow(params: object): void;
  setCustomUIConfig(params: object): void;
  setUserInfo(params: object): void;
  logout(): void;
  cleanCache(): void;
  getUnreadCountCallback(): Promise<number>;
  setUrlClickWithEventName(eventName: string): void;
  setUnreadCountWithEventName(eventName: string): void;
};

const { Qiyu } = NativeModules;

export default Qiyu as QiyuType;
