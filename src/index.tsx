import { NativeModules } from 'react-native';

type QiyuType = {
  multiply(a: number, b: number): Promise<number>;
  registerAppId(
    appKey: string,
    appName: string,
    callback: void
  ): Promise<number>;
  openServiceWindow(params: object): void;
  setCustomUIConfig(params: object): void;
  setUserInfo(params: object, callback: void): Promise<number>;
  logout(): void;
  cleanCache(): void;
  getUnreadCountCallback(): Promise<number>;
  setUrlClickWithEventName(eventName: string): void;
  setUnreadCountWithEventName(eventName: string): void;
};

const { Qiyu } = NativeModules;

export default Qiyu as QiyuType;
