<script lang="ts" setup>
import type { ColumnDef } from '@tanstack/vue-table'
import { h } from 'vue'

import DataTable from '@/components/DataTable.vue'
interface Payment {
    id: string
    amount: number
    status: 'pending' | 'processing' | 'success' | 'failed'
    email: string
}

const payments: Payment[] = [
    {
        id: '728ed52f',
        amount: 100,
        status: 'pending',
        email: 'm@example.com',
    },
    {
        id: '489e1d42',
        amount: 125,
        status: 'processing',
        email: 'example@gmail.com',
    },
]


const columns: ColumnDef<Payment>[] = [
    {
        accessorKey: 'id',
        header: () => h('div', {}, 'ID'),
        cell: ({ row }) => {
            const id = row.getValue('id')
            return h('div', { class: 'font-medium' }, String(id))
        },
    },
    {
        accessorKey: 'status',
        header: () => h('div', {}, 'Status'),
        cell: ({ row }) => {
            const status = row.getValue('status')
            return h('div', { class: 'font-medium' }, String(status))
        },
    },
    {
        accessorKey: 'email',
        header: () => h('div', {}, 'Email'),
        cell: ({ row }) => {
            const email = row.getValue('email')
            return h('div', { class: 'font-medium' }, String(email))
        },
    },
    {
        accessorKey: 'amount',
        header: () => h('div', {}, 'Amount'),
        cell: ({ row }) => {
            const amount = Number.parseFloat(row.getValue('amount'))
            const formatted = new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: 'USD',
            }).format(amount)

            return h('div', { class: 'font-medium' }, formatted)
        },
    },
]

</script>

<template>
    <DataTable :columns="columns" :data="payments" />
</template>