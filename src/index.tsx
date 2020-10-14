import { NativeModules } from 'react-native';

type QiyuType = {
  multiply(a: number, b: number): Promise<number>;
};

const { Qiyu } = NativeModules;

export default Qiyu as QiyuType;
