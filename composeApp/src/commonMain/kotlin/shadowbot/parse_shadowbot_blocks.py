#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
解析影刀指令的具体内容
"""

import pickle
import json

SOURCE_PATH = r"C:\user-project\xiaokeer-python\shadowbot2\InstructData2"

def parse_shadowbot_data():
    """解析影刀流程块数据"""
    try:
        with open(SOURCE_PATH, 'rb') as f:
            clipboard_data = pickle.load(f)

        # 查找包含影刀流程块的格式
        for fmt_id, fmt_data in clipboard_data.items():
            format_name = fmt_data['format_name']
            data = fmt_data['data']

            # 找到 ShadowBot.Flow.Blocks 格式
            if format_name == "ShadowBot.Flow.Blocks":
                print("=" * 80)
                print("影刀流程块数据 (ShadowBot.Flow.Blocks)")
                print("=" * 80)
                print()

                # 解析二进制数据
                # 跳过前面的头部信息，找到JSON数据
                json_start = data.find(b'[{')
                if json_start != -1:
                    # 尝试找到JSON结束位置
                    json_bytes = data[json_start:]

                    # 尝试多种方式解析
                    blocks = None
                    json_data = json_bytes.decode('utf-8', errors='ignore')

                    # 尝试使用JSONDecoder逐个解析
                    from json import JSONDecoder
                    decoder = JSONDecoder()

                    try:
                        blocks, idx = decoder.raw_decode(json_data)
                        print(f"成功解析JSON数据 (使用了前 {idx} 个字符)\n")
                        if blocks:
                            print(f"共包含 {len(blocks)} 个流程块:\n")
                        else:
                            print("未能成功解析流程块")
                            return None

                        for i, block in enumerate(blocks, 1):
                            print(f"【流程块 {i}】")
                            print(f"  ID: {block.get('id')}")
                            print(f"  名称: {block.get('name')}")
                            print(f"  启用状态: {block.get('isEnabled')}")

                            # 显示输入参数
                            if 'inputs' in block and block['inputs']:
                                print(f"  输入参数:")
                                for key, val in block['inputs'].items():
                                    print(f"    - {key}: {val}")

                            # 显示输出参数
                            if 'outputs' in block and block['outputs']:
                                print(f"  输出参数:")
                                for key, val in block['outputs'].items():
                                    print(f"    - {key}: {val}")

                            print()

                        # 保存美化的JSON到文件
                        json_file = SOURCE_PATH + "_parsed.json"
                        with open(json_file, 'w', encoding='utf-8') as f:
                            json.dump(blocks, f, ensure_ascii=False, indent=2)
                        print(f"✅ 已保存解析结果到: {json_file}")

                        return blocks

                    except json.JSONDecodeError as e:
                        print(f"JSON解析失败: {e}")
                        print(f"原始数据: {json_data[:500]}")
                else:
                    print("未找到JSON数据")

        return None

    except Exception as e:
        print(f"解析失败: {e}")
        import traceback
        traceback.print_exc()
        return None

if __name__ == "__main__":
    parse_shadowbot_data()
