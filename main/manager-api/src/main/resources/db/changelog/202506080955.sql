-- Enable wake-up word acceleration on smart control panel
update `sys_params` set param_value = 'Hello Little Wisdom;Hello Little Ambition;Little Love classmate;Hello Little Xin;Hello Little New;Little Beauty classmate;Little Dragon Little Dragon;Meow Meow classmate;Little Bin Little Bin;Little Ice Little Ice;Hey Hello' where param_code = 'wakeup_words';
update `sys_params` set param_value = 'true' where param_code = 'enable_wakeup_words_response_cache';
 