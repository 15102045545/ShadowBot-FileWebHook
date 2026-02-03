# -*- coding: utf-8 -*-
"""
影刀流程指令文件修改脚本

用于修改影刀(ShadowBot)流程指令文件中 "设置变量" 指令的值。
输入文件为从影刀复制指令后通过剪贴板保存的pickle文件。

使用方法:
    python modify_shadowbot_instructions.py -i <输入文件> -o <输出文件> -var <变量名> -val <值>

示例:
    python modify_shadowbot_instructions.py -i input.pkl -o output.pkl -var triggerId -val 1
    python modify_shadowbot_instructions.py -i input.pkl -o output.pkl -var triggerId -val 1 -var triggerFilesPath -val "C:/path"

输出:
    JSON 格式的结果，包含 success, message, outputPath 字段
"""

import json
import re
import base64
import argparse
import pickle
import sys
import os
from json import JSONDecoder
from typing import Dict, List, Any, Optional


class ShadowBotInstructionModifier:
    """
    影刀流程指令修改器

    负责读取pickle格式的剪贴板数据文件，修改其中的变量值，并保存。
    支持同时更新 ShadowBot.Flow.Blocks 格式和 HTML Format 中的 base64 数据。
    """

    def __init__(self, file_path: str):
        self.file_path = file_path
        self.clipboard_data: Dict[int, Dict[str, Any]] = {}
        self.instructions: List[Dict[str, Any]] = []
        self.error_message: str = ""

        # 用于保留二进制数据的头尾（确保数据完整性）
        self.blocks_format_id: Optional[int] = None
        self.data_header: bytes = b''
        self.data_footer: bytes = b''
        self.json_start: int = 0
        self.json_end: int = 0

    def load_file(self) -> bool:
        """加载pickle格式的剪贴板数据文件"""
        try:
            if not os.path.exists(self.file_path):
                self.error_message = f"输入文件不存在: {self.file_path}"
                return False

            with open(self.file_path, 'rb') as f:
                self.clipboard_data = pickle.load(f)
            return True
        except Exception as e:
            self.error_message = f"无法读取文件: {e}"
            return False

    def extract_json_instructions(self) -> bool:
        """
        从剪贴板数据中提取JSON格式的指令数据

        使用 JSONDecoder.raw_decode 精确解析JSON边界，
        同时保留二进制数据的头尾部分以确保数据完整性。
        """
        try:
            for fmt_id, fmt_data in self.clipboard_data.items():
                format_name = fmt_data.get('format_name', '')
                data = fmt_data.get('data', b'')

                if format_name == "ShadowBot.Flow.Blocks" and isinstance(data, bytes):
                    self.blocks_format_id = fmt_id

                    # 定位JSON数据起始位置
                    json_start = data.find(b'[{')
                    if json_start != -1:
                        json_bytes = data[json_start:]
                        json_data = json_bytes.decode('utf-8', errors='ignore')

                        # 精确解析JSON边界
                        decoder = JSONDecoder()
                        self.instructions, json_end = decoder.raw_decode(json_data)

                        # 保存头部和尾部
                        self.json_start = json_start
                        self.json_end = json_end
                        self.data_header = data[:json_start]
                        self.data_footer = data[json_start + json_end:]

                        return True

            self.error_message = "未找到 ShadowBot.Flow.Blocks 格式的指令数据"
            return False

        except json.JSONDecodeError as e:
            self.error_message = f"JSON解析失败: {e}"
            return False
        except Exception as e:
            self.error_message = f"提取指令失败: {e}"
            return False

    def modify_variable(self, variable_name: str, new_value: str) -> bool:
        """
        修改指定变量的值

        查找 programing.variable 类型的指令，根据变量名匹配并更新值。
        值格式为 "类型代码:值"，字符串类型代码为 10。
        """
        modified = False
        for instr in self.instructions:
            if instr.get('name') == 'programing.variable':
                var_info = instr.get('outputs', {}).get('variable', {})
                if var_info.get('name') == variable_name:
                    var_type = var_info.get('type', 'str')
                    type_prefix = '10' if var_type == 'str' else '10'

                    instr['inputs']['value']['value'] = f"{type_prefix}:{new_value}"
                    modified = True
                    break

        return modified

    def update_clipboard_data(self) -> None:
        """
        将修改后的指令更新回剪贴板数据结构

        1. 更新 ShadowBot.Flow.Blocks: 保留原始二进制头尾，仅替换中间JSON部分
        2. 更新 HTML Format: 替换其中的 base64 编码数据
        """
        new_json_str = json.dumps(self.instructions, ensure_ascii=False, separators=(',', ':'))
        new_json_bytes = new_json_str.encode('utf-8')
        new_base64_data = base64.b64encode(new_json_bytes).decode('utf-8')

        # 更新 ShadowBot.Flow.Blocks（保留头尾二进制数据）
        if self.blocks_format_id is not None:
            new_data = self.data_header + new_json_bytes + self.data_footer
            self.clipboard_data[self.blocks_format_id]['data'] = new_data

        # 更新 HTML Format 中的 base64 数据
        base64_pattern = r'("data":")([A-Za-z0-9+/=]+)(")'

        for fmt_id, fmt_data in self.clipboard_data.items():
            if fmt_id == self.blocks_format_id:
                continue

            format_name = fmt_data.get('format_name', '')
            data = fmt_data.get('data', b'')

            if isinstance(data, bytes):
                try:
                    content_str = data.decode('utf-8', errors='ignore')
                    if re.search(base64_pattern, content_str):
                        new_content = re.sub(base64_pattern, f'\\g<1>{new_base64_data}\\g<3>', content_str)
                        fmt_data['data'] = new_content.encode('utf-8')
                except Exception:
                    continue

            elif isinstance(data, str):
                if re.search(base64_pattern, data):
                    new_content = re.sub(base64_pattern, f'\\g<1>{new_base64_data}\\g<3>', data)
                    fmt_data['data'] = new_content

    def save_to_file(self, output_path: str) -> bool:
        """将修改后的剪贴板数据保存到文件"""
        try:
            self.update_clipboard_data()

            # 确保输出目录存在
            output_dir = os.path.dirname(output_path)
            if output_dir and not os.path.exists(output_dir):
                os.makedirs(output_dir, exist_ok=True)

            with open(output_path, 'wb') as f:
                pickle.dump(self.clipboard_data, f, protocol=pickle.HIGHEST_PROTOCOL)
            return True
        except Exception as e:
            self.error_message = f"保存失败: {e}"
            return False


def main():
    """主函数 - 输出 JSON 格式的结果"""
    result = {
        "success": False,
        "message": "",
        "outputPath": ""
    }

    parser = argparse.ArgumentParser(
        description='修改影刀流程指令文件中的变量值',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  python modify_shadowbot_instructions.py -i input.pkl -o output.pkl -var triggerId -val 1
  python modify_shadowbot_instructions.py -i input.pkl -o output.pkl -var triggerId -val 1 -var triggerFilesPath -val "C:/path"
        """
    )

    parser.add_argument('-i', '--input', required=True, help='输入文件路径')
    parser.add_argument('-o', '--output', required=True, help='输出文件路径')
    parser.add_argument('-var', '--variable', action='append', required=True, dest='variables', help='变量名 (可多次指定)')
    parser.add_argument('-val', '--value', action='append', required=True, dest='values', help='变量值 (与 -var 配对)')

    try:
        args = parser.parse_args()
    except SystemExit:
        result["message"] = "参数解析失败"
        print(json.dumps(result, ensure_ascii=False))
        return 1

    # 校验参数配对
    if len(args.variables) != len(args.values):
        result["message"] = "-var 和 -val 参数数量必须相同"
        print(json.dumps(result, ensure_ascii=False))
        return 1

    # 执行修改
    modifier = ShadowBotInstructionModifier(args.input)

    if not modifier.load_file():
        result["message"] = modifier.error_message
        print(json.dumps(result, ensure_ascii=False))
        return 1

    if not modifier.extract_json_instructions():
        result["message"] = modifier.error_message
        print(json.dumps(result, ensure_ascii=False))
        return 1

    # 修改所有变量
    modified_vars = []
    for var_name, var_value in zip(args.variables, args.values):
        if modifier.modify_variable(var_name, var_value):
            modified_vars.append(var_name)

    if not modifier.save_to_file(args.output):
        result["message"] = modifier.error_message
        print(json.dumps(result, ensure_ascii=False))
        return 1

    # 成功
    result["success"] = True
    result["message"] = f"指令修改并保存成功，已更新变量: {', '.join(modified_vars)}" if modified_vars else "指令保存成功（无变量被修改）"
    result["outputPath"] = args.output.replace("\\", "/")

    print(json.dumps(result, ensure_ascii=False))
    return 0


if __name__ == '__main__':
    sys.exit(main())
