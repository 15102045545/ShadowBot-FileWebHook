#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修改影刀指令并重新打包
"""

import pickle
import json
from json import JSONDecoder, JSONEncoder

SOURCE_PATH = r"C:\user-project\xiaokeer-python\shadowbot2\InstructData"
OUTPUT_PATH = r"C:\user-project\xiaokeer-python\shadowbot2\InstructData_modified"


def modify_shadowbot_instructions():
    """读取、修改并保存影刀指令"""
    try:
        # 1. 读取原始数据
        print("=" * 80)
        print("步骤1: 读取原始剪贴板数据")
        print("=" * 80)
        with open(SOURCE_PATH, 'rb') as f:
            clipboard_data = pickle.load(f)
        print(f"成功读取 {len(clipboard_data)} 种剪贴板格式\n")

        # 2. 找到并解析影刀流程块
        print("=" * 80)
        print("步骤2: 解析影刀流程块")
        print("=" * 80)

        blocks = None
        blocks_format_id = None

        for fmt_id, fmt_data in clipboard_data.items():
            format_name = fmt_data['format_name']
            data = fmt_data['data']

            if format_name == "ShadowBot.Flow.Blocks":
                blocks_format_id = fmt_id

                # 找到JSON数据起始位置
                json_start = data.find(b'[{')
                if json_start != -1:
                    json_bytes = data[json_start:]
                    json_data = json_bytes.decode('utf-8', errors='ignore')

                    # 解析JSON
                    decoder = JSONDecoder()
                    blocks, json_end = decoder.raw_decode(json_data)

                    print(f"找到 {len(blocks)} 个流程块")
                    print(f"原始数据头部长度: {json_start} 字节")
                    print(f"JSON数据长度: {json_end} 字节\n")

                    # 保存头部和尾部
                    data_header = data[:json_start]
                    data_footer = data[json_start + json_end:]

                    break

        if not blocks:
            print("未找到影刀流程块数据")
            return False

        # 3. 修改指令
        print("=" * 80)
        print("步骤3: 修改流程块参数")
        print("=" * 80)

        print("原始流程块:")
        for i, block in enumerate(blocks, 1):
            print(f"\n【流程块 {i}】")
            print(f"  名称: {block['name']}")
            if block['inputs']:
                print(f"  输入参数: {json.dumps(block['inputs'], ensure_ascii=False)}")

        # 示例修改：将 sleep 的时间从 2 秒改为 5 秒
        for block in blocks:
            if block['name'] == 'programing.sleep':
                print(f"\n找到 sleep 指令，修改参数...")
                print(f"  原始 seconds: {block['inputs']['seconds']['value']}")
                block['inputs']['seconds']['value'] = "10:5"  # 改为5秒
                print(f"  修改后 seconds: {block['inputs']['seconds']['value']}")

        # 可以添加更多修改逻辑
        # 例如：修改循环次数
        for block in blocks:
            if block['name'] == 'workflow.for':
                print(f"\n找到 for 循环，修改参数...")
                print(f"  原始 stop: {block['inputs']['stop']['value']}")
                block['inputs']['stop']['value'] = "10:3"  # 改为循环3次
                print(f"  修改后 stop: {block['inputs']['stop']['value']}")

        print("\n修改后流程块:")
        for i, block in enumerate(blocks, 1):
            print(f"\n【流程块 {i}】")
            print(f"  名称: {block['name']}")
            if block['inputs']:
                print(f"  输入参数: {json.dumps(block['inputs'], ensure_ascii=False)}")

        # 4. 重新打包数据
        print("\n" + "=" * 80)
        print("步骤4: 重新打包剪贴板数据")
        print("=" * 80)

        # 将修改后的JSON转回字符串
        new_json_str = json.dumps(blocks, ensure_ascii=False, separators=(',', ':'))
        new_json_bytes = new_json_str.encode('utf-8')

        # 重新组装完整数据
        new_data = data_header + new_json_bytes + data_footer

        print(f"原始数据大小: {len(data)} 字节")
        print(f"新数据大小: {len(new_data)} 字节")

        # 更新剪贴板数据
        clipboard_data[blocks_format_id]['data'] = new_data

        # 5. 保存修改后的数据
        print("\n" + "=" * 80)
        print("步骤5: 保存修改后的数据")
        print("=" * 80)

        with open(OUTPUT_PATH, 'wb') as f:
            pickle.dump(clipboard_data, f, protocol=pickle.HIGHEST_PROTOCOL)

        print(f"成功保存到: {OUTPUT_PATH}")
        print(f"\n提示: 可以使用粘贴脚本并指定新路径来使用修改后的指令")

        return True

    except Exception as e:
        print(f"修改失败: {e}")
        import traceback
        traceback.print_exc()
        return False


if __name__ == "__main__":
    modify_shadowbot_instructions()
    print("\n按回车键退出...")
    input()
