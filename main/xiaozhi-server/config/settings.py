import os
from config.config_loader import read_config, get_project_dir, load_config


default_config_file = "config.yaml"
config_file_valid = False


def check_config_file():
    global config_file_valid
    if config_file_valid:
        return
    """
    Simplified config check, only prompts the user about config file usage
    """
    custom_config_file = get_project_dir() + "data/." + default_config_file
    if not os.path.exists(custom_config_file):
        raise FileNotFoundError(
            "Cannot find data/.config.yaml file, please check the documentation to ensure this config file exists."
        )

    # Check if config is read from API
    config = load_config()
    if config.get("read_config_from_api", False):
        print("Reading config from API")
        old_config_origin = read_config(custom_config_file)
        if old_config_origin.get("selected_module") is not None:
            error_msg = "It looks like your config file contains both Console and local config:\n"
            error_msg += "\nSuggestions:\n"
            error_msg += "1. Copy config_from_api.yaml from the root directory to data/ and rename it to .config.yaml\n"
            error_msg += "2. Configure the API address and key as described in the documentation\n"
            raise ValueError(error_msg)
    config_file_valid = True
