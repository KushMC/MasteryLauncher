
//
// Created by maks on 05.06.2023.
//

#ifndef MasteryLauncher_NSBYPASS_H
#define MasteryLauncher_NSBYPASS_H

#include <stdbool.h>

bool linker_ns_load(const char* lib_search_path);
void* linker_ns_dlopen(const char* name, int flag);
void* linker_ns_dlopen_unique(const char* tmpdir, const char* name, int flag);

#endif //MasteryLauncher_NSBYPASS_H
