import { NativeModules } from 'react-native';

type QiyuType = {
  multiply(a: number, b: number): Promise<number>;
  registerAppId(appKey: string, appName: string): Promise<number>;
  openServiceWindow(params: object): void;
  setCustomUIConfig(params: object): void;
};

const { Qiyu } = NativeModules;

export default Qiyu as QiyuType;
